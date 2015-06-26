package es.cesga.hadoop.domain;

public enum NodeStatus {

	PENDING(1), ACTIVE(3), FAILED(7), POWEROFF(8);
	
	private int value;
	
	private NodeStatus(int value) {
		this.value = value;
	}
	
	/*
    <!-- STATE values,
    see http://opennebula.org/_media/documentation:rel3.6:states-complete.png

      INIT      = 0
      PENDING   = 1
      HOLD      = 2
      ACTIVE    = 3 In this state, the Life Cycle Manager state is relevant
      STOPPED   = 4
      SUSPENDED = 5
      DONE      = 6
      FAILED    = 7
      POWEROFF  = 8
      UNDEPLOYED = 9
    -->
    <xs:element name="STATE" type="xs:integer"/>

    <!-- LCM_STATE values, this sub-state is relevant only when STATE is
         ACTIVE (4)

      LCM_INIT            = 0,
      PROLOG              = 1,
      BOOT                = 2,
      RUNNING             = 3,
      MIGRATE             = 4,
      SAVE_STOP           = 5,
      SAVE_SUSPEND        = 6,
      SAVE_MIGRATE        = 7,
      PROLOG_MIGRATE      = 8,
      PROLOG_RESUME       = 9,
      EPILOG_STOP         = 10,
      EPILOG              = 11,
      SHUTDOWN            = 12,
      CANCEL              = 13,
      FAILURE             = 14,
      CLEANUP_RESUBMIT    = 15,
      UNKNOWN             = 16,
      HOTPLUG             = 17,
      SHUTDOWN_POWEROFF   = 18,
      BOOT_UNKNOWN        = 19,
      BOOT_POWEROFF       = 20,
      BOOT_SUSPENDED      = 21,
      BOOT_STOPPED        = 22,
      CLEANUP_DELETE      = 23,
      HOTPLUG_SNAPSHOT    = 24,
      HOTPLUG_NIC         = 25,
      HOTPLUG_SAVEAS           = 26,
      HOTPLUG_SAVEAS_POWEROFF  = 27,
      HOTPLUG_SAVEAS_SUSPENDED = 28,
      SHUTDOWN_UNDEPLOY   = 29,
      EPILOG_UNDEPLOY     = 30,
      PROLOG_UNDEPLOY     = 31,
      BOOT_UNDEPLOY       = 32
    -->
    */
}
