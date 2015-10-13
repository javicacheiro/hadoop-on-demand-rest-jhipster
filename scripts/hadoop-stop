#!/bin/bash
# vim:tabstop=2:autoindent:expandtab:shiftwidth=2
#
# Author:  Javier Cacheiro Lopez (jlopez@cesga.es)
# Purpose: Start a Hadoop cluster
# Usage:   $0 --option
# Return:  0 if success, 1 if error
#
# Changelog
#   12-09-2013 JLC
#     First version
#   11-10-2013 JLC
#     By default use hadoop cluster name from .hadoop/clustername
#   15-05-2014 JLC
#     REST options

scriptName="${0##*/}"

#
# Default values
#
#declare -i SIZE=10

# Hadoop-on-demand root directory
HADOOP_ROOT=$HOME/.hadoop-on-demand

#
# Functions
#
function printUsage() {
    cat <<EOF

Usage: $scriptName -c <cluster name>
    
Stop a Hadoop cluster 

options include:
    -c clusterID              Name of the cluster to stop: e.g. hadoop-1.1.2-9282
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

function yesno() {
  while true; do
      read -p "¿Está seguro? [s/N]" yn
      case $yn in
          [Ss]* ) break;;
          [Nn]* ) exit;;
          * ) echo "Por favor responda sí o no.";;
      esac
  done
}

#
# Stop VM instances in OpenNebula
# 
function stopCluster() {

  local LABEL=$1

  if [[ -z $LABEL ]]; then
    echo "ERROR: No se ha recibido correctamente el nombre del cluster"
  fi

  if [[ -z $REST ]]; then
    # Show the user the list of machines that will be deleted
    echo "Se eliminarán las siguientes máquinas:"
    onevm list m|grep $LABEL
    # Are you sure?
    yesno
  fi
    

  # Let's do it
  onevm list m|grep $LABEL|awk '{print $1}'|xargs -l1 onevm delete

  # Let's remove also the clustername file
  if [[ -r $HADOOP_ROOT/clustername ]]; then
    rm "$HADOOP_ROOT/clustername"
  fi

}

###############################################################################
#
# MAIN
#
###############################################################################

# Load config if available (overwrites previous default values)
if [[ -r $HADOOP_ROOT/config ]]; then
  . $HADOOP_ROOT/config
fi



readOptions "$@"

if [[ -z $CLUSTERID ]]; then
  # Get default NAME for the cluster to stop from ~/.hadoop-on-demand/clustername
  if [[ -r $HADOOP_ROOT/clustername ]]; then
    NAME=`cat $HADOOP_ROOT/clustername`
  fi
else
  NAME=hadoop-$CLUSTERID
fi

if [[ -z $NAME ]];then
  echo "ERROR: No se ha encontrado el nombre de ningun cluster para parar"
  exit 2;
else
  stopCluster $NAME
fi
