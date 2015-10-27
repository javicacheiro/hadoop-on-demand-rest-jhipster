#!/bin/bash
# vim:tabstop=2:autoindent:expandtab:shiftwidth=2
#
# Author:  Javier Cacheiro Lopez (jlopez@cesga.es)
# Purpose: Get the status of a Hadoop cluster
# Usage:   $0 --option
# Return:  0 if success, 1 if error
#
# Changelog
#   11-10-2013 JLC
#     First version
#   15-05-2014 JLC
#     REST options
#   12-06-2014 JLC
#     Support for master with 2 IPs

# Hadoop-on-demand root directory
HADOOP_ROOT=$HOME/.hadoop-on-demand

#
# Functions
#
function printUsage() {
    cat <<EOF

Usage: $scriptName -c <clusterID>
    
Stop a Hadoop cluster 

options include:
    -c clusterID              Cluster ID of the Hadoop cluster: 
                              i.e. 9282 
                              which means the label of the cluster will be hadoop-9282
    -R                        REST API mode (only for use within REST service calls)
    -h                        Print help

EOF
}


#
#  Read options
#
function readOptions() {
  while getopts ":hc:R" opt; do
      case "$opt" in
          c) CLUSTERID=$OPTARG ;;
          R) REST="yes" ;;
          h) printUsage; exit 1 ;;
          *) printUsage; exit 1 ;;
      esac
  done

  # Check that there are no unparsed options and that we have the options we need
  shift $((OPTIND - 1))
  if [[ $# != 0 ]]; then
      printUsage
      exit 1
  fi
}

#
# MAIN
#

readOptions "$@"

# Load config if available (overwrites previous default values)
if [[ -r $HADOOP_ROOT/config ]]; then
  . $HADOOP_ROOT/config
fi

if [[ -z $REST ]]; then
  cd $HADOOP_ROOT
  #MASTER=`head -n1 node.list`
  MASTER=`cat master`
else
  if [[ -z $CLUSTERID ]]; then
    echo "ERROR: No se ha especificado el Cluster ID."
  else
    # Get the IP of the master node from OpenNebula
    MASTER=`onevm list|grep hadoop-$CLUSTERID-0|awk '{print $1}' | xargs -l1 oneip`
  fi
fi


ssh hadoop@$MASTER '
echo "==> Hadoop cluster status"
hadoop dfsadmin -report | grep "Datanodes available"
echo -ne "Tasktrackers available "
hadoop job -list-active-trackers|wc -l
echo "===> HDFS"
hadoop dfsadmin -report
echo "===> Task trackers"
hadoop job -list-active-trackers
'
