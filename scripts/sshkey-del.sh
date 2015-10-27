#!/bin/bash

### EXIT CODES
##### 0 Success
##### 1 Insufficient arguments
##### 2 Too many arguments
##### 4 Unexpected Error

##### VARS #####
################
USER=$1
KEY=$2
NODE_ADDRESS=$3
akp=`ssh -l $USER $NODE_ADDRESS "echo ~/.ssh/authorized_keys"` # authorized_keys path

##### FUNCTIONS #####
#####################
function print_help {
	echo -e 'sshkey-del syntax:'
	echo -e "\t"'sshkey-del "key"'
}


#####################
#####  M A I N  #####
#####################

#set -x

if [ $1 == '-h' ] 2>/dev/null || [ $1 == '--help' ] 2> /dev/null; then
	print_help
	exit 0
fi

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




nlbd=`ssh -l $USER $NODE_ADDRESS "cat $akp | wc -l"` # Number of Lines Before Delete

## -- DELETE -- ##


ssh -l $USER $NODE_ADDRESS  << EOF
  grep -v "$KEY" $akp > $akp".tmp";
  cat $akp".tmp" > $akp;
  rm $akp".tmp"
EOF


nlad=`ssh -l $USER "$NODE_ADDRESS" "cat $akp | wc -l"` # Number of Lines After Delete
ndl=`echo $nlbd ' - ' $nlad | bc` # Number of Deleted Lines

if [ $ndl -gt 1 ]; then
	echo $ndl lines were deleted!
fi 

if [ $ndl == 1 ]; then
	echo $ndl line was deleted!
fi

if [ $ndl == 0 ]; then
	echo no lines were deleted!
fi

if [ $ndl -lt 0 ]; then
	echo 'UNEXPECTED ERROR OCURRED'
	exit 4
fi

exit 0
