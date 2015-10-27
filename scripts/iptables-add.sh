#!/bin/bash

#################
### FUNCTIONS ###
#################
function print_help {
	echo 'iptables-add'
	echo -e "\t"'hadoop-iptables -h'
	echo -e "\t\t"'Shows this help'
	echo -e "\t"'iptables-add IP'
	echo -e "\t\t"'Adds the given IP to iptables'
}


###################
### EXIT VALUES ###
###################
# 0 - Success
# 1 - Insufficient parameters
# 2 - Too many parameters



### PARSE CMD ###
IP=$1
NODE_ADDRESS=$2

if [ $1 == '-h' ] || [ $1 == '--help' ]; then
	print_help
	exit 0;
fi;

if [ $# -ne 2 ]; then
	echo 'Bad number of parameters'
	print_help
	exit 1
fi



ocurrences=$(ssh root@$NODE_ADDRESS "iptables -L -n --line-numbers | grep $IP | wc -c")

if [ $ocurrences -lt 1 ]; then
	ssh -l root $NODE_ADDRESS "iptables -I INPUT 1 -s $IP -j ACCEPT"
fi
