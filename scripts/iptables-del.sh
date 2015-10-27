#!/bin/bash

#################
### FUNCTIONS ###
#################
function print_help {
	echo 'iptables-del'
	echo -e "\t"'hadoop-iptables -h'
	echo -e "\t\t"'Shows this help'
	echo -e "\t"'iptables-del IP'
	echo -e "\t\t"'Removes the given IP from iptables'
}

###################
### EXIT VALUES ###
###################
# 0 - Success
# 1 - Insufficient parameters
# 2 - Too many parameters


if [ $1 == '-h' ] || [ $1 == '--help' ]; then
	print_help
	exit 0;
fi;


### PARSE CMD ###
IP=$1
NODE_ADDRESS=$2


if [ $# -ne 2 ]; then
	echo 'Wrong number of parameters'
	print_help
	exit 1
fi

#Removes a possible /32 at the end of the address
IP=${IP/\/32/}

input_rules=`ssh -l root $NODE_ADDRESS "iptables -L INPUT --line-numbers -n" | awk  '$5 == "'$IP'" {print $1}'`

# We sort then in reverse order so we can remove then without displacing the rest of rules
for i in `echo $input_rules | tr ' ' '\n' | sort -r -n`; do
	ssh -l root $NODE_ADDRESS "iptables -D INPUT $i"
done
