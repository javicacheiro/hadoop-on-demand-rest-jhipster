#!/bin/bash
# vim:tabstop=2:autoindent:expandtab:shiftwidth=2
#
# Author:  Javier Cacheiro Lopez (jlopez@cesga.es)
# Purpose: Start a Hadoop cluster
# Usage:   $0 --option
# Return:  0 if success, 1 if unknow options, 2 cluster already running
#
# Changelog
#   19-07-2013 JLC
#     First version
#   12-09-2013 JLC
#     Parallel version
#   18-09-2013 JLC
#     Corrected bug in startCluster
#   11-10-2013 JLC
#     Remove password connection
#     Connection using SSH public key
#   15-10-2013 JLC
#     Use different template for master
#   09-01-2014 JLC
#     Check if there is a cluster already registered, then exit
#   08-05-2014 JLC
#     REST options definition
#   15-05-2014 JLC
#     REST options implementation
#     Create HDFS dir: /user/hadoop
#   06-06-2014 JLC
#     Hadoop 1.2.1 support
#   12-06-2014 JLC
#     Support for a master with 2 IPs: one public and one private
#
# TODO
#   TODO1: Implement replication size, blocksize and rtasks cmdline options and conf options
#            hdfs-site dfs.block.size 16777216
#            dfs-replication 3
#            mapred.reduce.tasks "95%*<tast_trackers>"
#   TODO2: Link /home/hadoop to /scratch/hadoop-home directory
#   TODO3: añadir a /etc/init.d/context: barriers=0 o nobarrier
#   TOOD4: Adapt script to the fact that now hadoop-master has 2 IPs

scriptName="${0##*/}"

#
# Default values
#
declare -i SIZE=3
declare -i REPLICATION=3
declare -i BLOCKSIZE=16777216
declare -i RTASKS=1

# Location of the check_ssh nagios binary (can be overwritten in config)
CHECK_SSH=/usr/local/bin/check_ssh

# Hadoop-on-demand root directory
HADOOP_ROOT=$HOME/.hadoop-on-demand

# Name of the log file
LOG=$HADOOP_ROOT/hadoop-on-demand.log.`date '+%Y%m%d_%H%M'`

# Load config if available (overwrites previous default values)
if [[ -r $HADOOP_ROOT/config ]]; then
  . $HADOOP_ROOT/config
fi

#
# Functions
#
function printUsage() {
    cat <<EOF

Usage: $scriptName [-s SIZE] [-r dfs.replication] [-b  <dfs.block.size>] [-t <mapred.reduce.tasks>]
    
Start a Hadoop cluster 

options include:
    -s SIZE                   Number of slaves in the Hadoop cluster (default 3)
    -r dfs.replication        Number of replicas of each file (default 3)
    -b dfs.block.size         HDFS block size (default 16MB)
    -t mapred.reduce.tasks    Number of reduce tasks (default 1)
    -c clusterID              Cluster ID (only for use within REST service calls)
    -R                        REST API mode (only for use within REST service calls)
    -h                        Print help

EOF
}


#
#  Read options
#
function readOptions() {
  while getopts ":hs:r:b:t:c:R" opt; do
      case "$opt" in
          s) SIZE=$OPTARG ;;
          r) REPLICATION=$OPTARG ;;
          b) BLOCKSIZE=$OPTARG ;;
          t) RTASKS=$OPTARG ;;
          c) CLUSTERID=$OPTARG ;;
          R) REST="yes" ;;
          h) printUsage; exit 1 ;;
          *) printUsage; exit 1 ;;
      esac
  done

  # Check that there are no unparsed options and that we have the options we need
  shift $((OPTIND - 1))
  if [[ $# != 0 || -z $SIZE ]]; then
      printUsage
      exit 1
  fi
}

function progressbar {

  local done=$1
  local total=$2

  # Set desired column width
  local width=72

  # Normalize to the selected column width
  done=$(($width*$done/$total))
  local pending=$(($width-$done))

  # Fill done bar
  local donebar=''
  for (( b=0; b<$done; b++ )); do
    donebar+="="
  done

  # Fill pending bar
  local pendingbar=''
  for (( f=0; f<$pending; f++ )); do
     pendingbar+="."
  done

  percentage=$(printf '%2d' $((100*$done/$width)))
  echo -ne " $percentage% [$donebar>$pendingbar]\r"

}


#
# Start VM instances in OpenNebula
# 
function startCluster() {
  echo "Desplegando las maquinas en OpenNebula"
  # master
  echo "x$LABEL"
  onetemplate instantiate 265 --name $LABEL-0
  # slaves
  for i in `seq 1 $SIZE`; do onetemplate instantiate 254 --name $LABEL-$i;done
  # --raw "SSH_KEY=\"`cat ~/.ssh/id_dsa.pub`\"

  # Waiting for all nodes to start
  echo "Esperando a que arranquen los nodos"
  echo ""
  STARTED=0
  while [[ $STARTED -lt $TOTAL ]]; do
    sleep 10
    STARTED=`onevm list m | grep $LABEL | awk '{print $5}'| grep runn | wc -l`
    if [[ -z $REST ]]; then
      progressbar $STARTED $TOTAL
    else
      echo "  ... arrancados $STARTED / $TOTAL"
    fi
  done
  echo -e "\n\n"
}

#
# Configure Hadoop Node
#
function configureNode() {
  local node=$1
  local nodename=$2

  echo "Configurando el nodo $node"

  # First we have to check the node has already completed the boot process and ssh is available
  #OK=1
  #while [[ $OK -ne 0 ]]; do
  #  $CHECK_SSH -t 10 $node
  #  OK=$?
  #done

  until scp node.list root@$node: ;do
    echo "Esperando por el nodo $node"
    sleep 10
  done

  #scp iptables root@$node:/etc/sysconfig/iptables
  #scp conf/* root@$node:/opt/cesga/hadoop/conf/
  #scp version root@$node:/opt/cesga/modules/hadoop/.version

  echo "... hadoop"
  ssh root@$node <<EOF
        # Commands below are already included in the init.d context script
        #echo " ... scratch"
        #mkfs.ext4 /dev/vdc
        #mount /dev/vdc /scratch/ -o noatime
        #mkdir /scratch/hadoop
        #chown hadoop:hadoop /scratch/hadoop
        #mkdir /scratch/hadoop/logs
        #chown hadoop:hadoop /scratch/hadoop/logs
        #ln -s /scratch/hadoop/logs /opt/cesga/hadoop/logs

        # Regeneate hosts file
        echo "127.0.0.1   localhost localhost.localdomain localhost4 localhost4.localdomain4" > /etc/hosts
        echo "::1         localhost localhost.localdomain localhost6 localhost6.localdomain6" >> /etc/hosts
        MASTER=`head -n1 node.list`
        # /etc/hosts
        echo "\$MASTER hadoop-master" >> /etc/hosts

        # Generate hadoop slaves
        sed '1d' node.list > /opt/cesga/hadoop/conf/slaves

        n=1
        for slave in \`cat /opt/cesga/hadoop/conf/slaves\`; do
                #echo "\$slave hadoop-`printf '%02d' \$n`" >> /etc/hosts
                echo "\$slave hadoop-\$n" >> /etc/hosts
                let "n++"
        done

        # Change hostname (it is afterwards displayed in hadoop monitoring page)
        hostname $nodename
        cp /etc/sysconfig/network /etc/sysconfig/network.0
        sed "s/HOSTNAME=localhost/HOSTNAME=$nodename/" /etc/sysconfig/network.0 > /etc/sysconfig/network

	# Firewall
        for node in \`cat node.list\`; do
                iptables -I INPUT 1 -s \$node -j ACCEPT
        done

        #service iptables restart
EOF
}

#
# Configure Hadoop master
#
function configureMaster() {
  local master=$1
  ssh root@$master "
    #echo StrictHostKeyChecking no >> /etc/ssh/ssh_config
    su - hadoop -c '
      #echo module load hadoop >> /home/hadoop/.bashrc
      #module load hadoop
      cd /opt/cesga/hadoop/conf/
      cp hadoop-env.sh hadoop-env.sh.0
      sed 's/HADOOP_HEAPSIZE=512/HADOOP_HEAPSIZE=1024/' hadoop-env.sh.0 > hadoop-env.sh
      cp hdfs-site.xml hdfs-site.xml.0
      sed 's/1048576/$BLOCKSIZE/' hdfs-site.xml.0 > hdfs-site.xml
      cp mapred-site.xml mapred-site.xml.0
      sed 's/9/$RTASKS/' mapred-site.xml.0 > mapred-site.xml
      hadoop namenode -format
      cd /opt/cesga/hadoop/bin
      #./start-dfs.sh
      #./start-mapred.sh
      ./start-all.sh
      sleep 20
      hadoop fs -mkdir /user/hadoop
    '
    "
}

#
# Get node list and store it in the file node.list and in the variable NODES
#
function getNodeList() {
  #onevm list m|grep hadoop112|awk '{print $1}'|xargs -l1 onevm show |grep IP= |sed 's/.*IP="//'|sed 's/",//' > node.list
  #onevm list m | grep $LABEL | awk '{print $1}'|xargs -l1 onevm show |grep IP= | grep -Eo '[0-9.]+' > node.list
  #onevm list m | grep $LABEL | awk '{print $1}'|xargs -l1 oneip > node.list
  VM_IDS=`onevm list m | grep $LABEL | awk '{print $1}'`
  master=true
  for ID in $VM_IDS; do
    if [ "$master" = true ]; then
      MASTER_PUB=`oneip $ID`
      echo $MASTER_PUB > master
      onevm show $ID -x | grep '<IP>' | grep -o '[0-9.]\+' | tail -n1 > node.list
      master=false
      NODES=$MASTER_PUB
    else
      oneip $ID >> node.list
      NODES="$NODES `oneip $ID`"
    fi
  done
  #tail -n+2 all.list > node.list
  #NODES=`cat node.list`
}

###############################################################################
#
# MAIN
#
###############################################################################

readOptions "$@"

# Total number of VM to start is SIZE+1
TOTAL=$((SIZE+1))

# Label for the cluster
if [[ -z $CLUSTERID ]]; then
  LABEL=hadoop-1.2.1-$$
else
  LABEL=hadoop-$CLUSTERID
fi

if [[ -z $REST ]]; then
  # Move to hadoop-on-demand root directory
  cd $HADOOP_ROOT

  if [[ -r clustername ]]; then
    # A cluster is already registered so we stop here
    echo
    echo "¡Ya tiene un cluster Hadoop en ejecucion!"
    echo "Solo se permite un cluster por usuario"
    echo "Para parar el cluster actual puede utilizar:"
    echo "    hadoop-stop"
    echo
    exit 2
  else
    # Save the label for later use
    echo $LABEL > clustername
  fi
else
  # If we are running in REST mode 
  #
  # We do not store the cluster name
  #
  # We return the LABEL of the new cluster as expected
  # by the rest service
  echo name:$LABEL
  
  # we do not need the log file
  LOG=/dev/null
fi

exec &> /home/jonatan/log.txt


# Start the VMs needed (SIZE+1)
date
echo "= Paso 1/3: Arrancando el cluster ="
startCluster
echo "Arranque del cluster Finalizado."
date
echo ""


# Get list of nodes
echo "= Paso 2/3: Obteniendo la lista de nodos ="
getNodeList
echo "Lista de nodos obtenida."
date
echo ""

# Configure Hadoop
echo "= Paso 3/3: Configurando Hadoop ="
N=0

for node in $NODES; do
  # Set node name
  if [[ $N == 0 ]]; then
	  nodename="hadoop-master"
	  let "N++"
  else
	  #nodename="hadoop-`printf '%02d' $N`"
	  nodename="hadoop-$N"
	  let "N++"
  fi

  configureNode $node $nodename >> $LOG 2>&1 &

done

echo "Esperando a que todos los slave esten configurados"
# Wait until all nodes are configured
wait

echo "Configurando el master del cluster"
configureMaster $MASTER_PUB >> $LOG 2>&1

# Wait a little bit for complete hadoop startup
sleep 10

echo "" >> $LOG
echo "-------------- HADOOP STATUS ------------------------------" >> $LOG
ssh hadoop@$MASTER_PUB '
. /etc/profile
echo "==> Hadoop cluster status"
hadoop dfsadmin -report | grep "Datanodes available"
echo -ne "Tasktrackers available "
hadoop job -list-active-trackers|wc -l
echo "===> HDFS"
hadoop dfsadmin -report
echo "===> Task trackers"
hadoop job -list-active-trackers
' >> $LOG 2>&1
echo "-------------- HADOOP STATUS ------------------------------" >> $LOG

date >> $LOG

# Configuration finished
cat <<EOF

-----------------------------------------------------------------------

¡Configuracion finalizada!"

-----------------------------------------------------------------------

Ya puede conectarse al nuevo cluster de hadoop a traves mediante ssh:
  ssh hadoop@$MASTER_PUB
   

Tambien monitorizar el estado del cluster en las siguientes direcciones:
    JobTracker Web Interface:  http://$MASTER_PUB:50030/jobtracker.jsp
    NameNode Web Interface:    http://$MASTER_PUB:50070/dfshealth.jsp

-----------------------------------------------------------------------

En caso de problemas no dude en contactar con el Dep. de Sistemas:
   Email:    sistemas@cesga.es
   Telefono: 981569810

-----------------------------------------------------------------------
EOF

date

