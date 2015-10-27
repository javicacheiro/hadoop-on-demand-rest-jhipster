#!/bin/bash

### EXIT CODES
##### 0 Success
##### 1 Insufficient arguments
##### 2 Too many arguments

##### VARS #####
################
USER=$1
KEY=$2
NODE_ADDRESS=$3
akp=`ssh -l $USER $NODE_ADDRESS "echo ~/.ssh/authorized_keys"` # authorized_keys path
akop=`ssh -l $USER $NODE_ADDRESS "echo ~/.ssh/authorized_keys.original"` # original authorized_keys path

##### FUNCTIONS #####
#####################
function print_help {
	echo -e 'sshkey-add syntax:'
	echo -e "\t"'sshkey-add "key"'
}

set -x
#####################
#####  M A I N  #####
#####################
if [ $# -lt 3 ]; then
	echo 'Insufficient arguments'
	print_help
	exit 1
fi

if [ $# -gt 3 ]; then
	echo 'Too much arguments'
	print_help
	exit 2
fi

if [ $1 == '-h' ] 2>/dev/null || [ $1 == '--help' ] 2> /dev/null; then
	print_help
fi

if [ ! -r $akop ]; then
	if [ -r $akp ]; then
		ssh -l $USER $NODE_ADDRESS "cp -f $akp $akop"
	fi
fi

# authorized keys content length
akcl=$(ssh -l $USER $NODE_ADDRESS "cat $akp 2>/dev/null | wc -c") 

if [ $akcl -lt 1 ]; then
	if [ -r $akop ]; then
		ssh -l $USER $NODE_ADDRESS "cat $akop > $akp"
	fi
fi

if [ $(ssh -l $USER $NODE_ADDRESS "cat $akp 2>/dev/null | grep "$KEY" | wc -c") -gt 0 ]; then # If key exists
	echo 'Given key ('"$KEY"') already exists'
else
	ssh -l $USER $NODE_ADDRESS "echo $KEY >> $akp"
	echo 'sshkey succesfully added!'
fi


exit 0
