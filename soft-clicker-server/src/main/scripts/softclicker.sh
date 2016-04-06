#!/usr/bin/env bash
# ----------------------------------------------------------------------------
# Main Script for the Soft Clicker Server
#
# Environment Variable Prequisites
#
#   DISPLAY_AGENT_HOME   Home of SoftClicker Server. If not set I will  try
#                   to figure it out.
#
#   JAVA_HOME       Must point at your Java Development Kit installation.
#
#   JAVA_OPTS       (Optional) Java runtime options used when the commands
#                   is executed.
#
# NOTE: Borrowed generously from Apache Tomcat startup scripts.
# -----------------------------------------------------------------------------

# OS specific support.  $var _must_ be set to either true or false.
#ulimit -n 100000

cygwin=false;
darwin=false;
os400=false;
mingw=false;
case "`uname`" in
CYGWIN*) cygwin=true;;
MINGW*) mingw=true;;
OS400*) os400=true;;
Darwin*) darwin=true
        if [ -z "$JAVA_VERSION" ] ; then
             JAVA_VERSION="CurrentJDK"
           else
             echo "Using Java version: $JAVA_VERSION"
           fi
           if [ -z "$JAVA_HOME" ] ; then
             JAVA_HOME=/System/Library/Frameworks/JavaVM.framework/Versions/${JAVA_VERSION}/Home
           fi
           ;;
esac

# resolve links - $0 may be a softlink
PRG="$0"

while [ -h "$PRG" ]; do
  ls=`ls -ld "$PRG"`
  link=`expr "$ls" : '.*-> \(.*\)$'`
  if expr "$link" : '.*/.*' > /dev/null; then
    PRG="$link"
  else
    PRG=`dirname "$PRG"`/"$link"
  fi
done

# Get standard environment variables
PRGDIR=`dirname "$PRG"`

# Only set DISPLAY_AGENT_HOME if not already set
[ -z "$SOFTCLICKER_HOME" ] && SOFTCLICKER_HOME=`cd "$PRGDIR/.." ; pwd`

# Set AXIS2_HOME. Needed for One Click JAR Download
AXIS2_HOME=$SOFTCLICKER_HOME

# For Cygwin, ensure paths are in UNIX format before anything is touched
if $cygwin; then
  [ -n "$JAVA_HOME" ] && JAVA_HOME=`cygpath --unix "$JAVA_HOME"`
  [ -n "$SOFTCLICKER_HOME" ] && SOFTCLICKER_HOME=`cygpath --unix "$SOFTCLICKER_HOME"`
  [ -n "$AXIS2_HOME" ] && SOFTCLICKER_HOME=`cygpath --unix "$SOFTCLICKER_HOME"`
fi

# For OS400
if $os400; then
  # Set job priority to standard for interactive (interactive - 6) by using
  # the interactive priority - 6, the helper threads that respond to requests
  # will be running at the same priority as interactive jobs.
  COMMAND='chgjob job('$JOBNAME') runpty(6)'
  system $COMMAND

  # Enable multi threading
  QIBM_MULTI_THREADED=Y
  export QIBM_MULTI_THREADED
fi

# For Migwn, ensure paths are in UNIX format before anything is touched
if $mingw ; then
  [ -n "$SOFTCLICKER_HOME" ] &&
    SOFTCLICKER_HOME="`(cd "$SOFTCLICKER_HOME"; pwd)`"
  [ -n "$JAVA_HOME" ] &&
    JAVA_HOME="`(cd "$JAVA_HOME"; pwd)`"
  [ -n "$AXIS2_HOME" ] &&
    SOFTCLICKER_HOME="`(cd "$SOFTCLICKER_HOME"; pwd)`"
  # TODO classpath?
fi

if [ -z "$JAVACMD" ] ; then
  if [ -n "$JAVA_HOME"  ] ; then
    if [ -x "$JAVA_HOME/jre/sh/java" ] ; then
      # IBM's JDK on AIX uses strange locations for the executables
      JAVACMD="$JAVA_HOME/jre/sh/java"
    else
      JAVACMD="$JAVA_HOME/bin/java"
    fi
  else
    JAVACMD=java
  fi
fi

if [ ! -x "$JAVACMD" ] ; then
  echo "Error: JAVA_HOME is not defined correctly."
  echo " SOFTCLICKER cannot execute $JAVACMD"
  exit 1
fi

# if JAVA_HOME is not set we're not happy
if [ -z "$JAVA_HOME" ]; then
  echo "You must set the JAVA_HOME variable before running SOFTCLICKER."
  exit 1
fi

if [ -e "$SOFTCLICKER_HOME/softclicker.pid" ]; then
  PID=`cat "$SOFTCLICKER_HOME"/softclicker.pid`
fi

# ----- Process the input command ----------------------------------------------
args=""
for c in $*
do
    if [ "$c" = "--debug" ] || [ "$c" = "-debug" ] || [ "$c" = "debug" ]; then
          CMD="--debug"
          continue
    elif [ "$CMD" = "--debug" ]; then
          if [ -z "$PORT" ]; then
                PORT=$c
          fi
    elif [ "$c" = "--stop" ] || [ "$c" = "-stop" ] || [ "$c" = "stop" ]; then
          CMD="stop"
    elif [ "$c" = "--start" ] || [ "$c" = "-start" ] || [ "$c" = "start" ]; then
          CMD="start"
    elif [ "$c" = "--version" ] || [ "$c" = "-version" ] || [ "$c" = "version" ]; then
          CMD="version"
    elif [ "$c" = "--restart" ] || [ "$c" = "-restart" ] || [ "$c" = "restart" ]; then
          CMD="restart"
    elif [ "$c" = "--test" ] || [ "$c" = "-test" ] || [ "$c" = "test" ]; then
          CMD="test"
    else
        args="$args $c"
    fi
done

if [ "$CMD" = "--debug" ]; then
  if [ "$PORT" = "" ]; then
    echo " Please specify the debug port after the --debug option"
    exit 1
  fi
  if [ -n "$JAVA_OPTS" ]; then
    echo "Warning !!!. User specified JAVA_OPTS will be ignored, once you give the --debug option."
  fi
  CMD="RUN"
  JAVA_OPTS="-Xdebug -Xnoagent -Djava.compiler=NONE -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=$PORT"
  echo "Please start the remote debugging client to continue..."
elif [ "$CMD" = "start" ]; then
  if [ -e "$SOFTCLICKER_HOME/softclicker.pid" ]; then
    if  ps -p $PID > /dev/null ; then
      echo "Process is already running"
      exit 0
    fi
  fi
  export SOFTCLICKER_HOME=$SOFTCLICKER_HOME
# using nohup sh to avoid erros in solaris OS.TODO
  nohup sh $SOFTCLICKER_HOME/bin/softclicker.sh $args > /dev/null 2>&1 &
  exit 0
elif [ "$CMD" = "stop" ]; then
  export SOFTCLICKER_HOME=$SOFTCLICKER_HOME
  kill -term `cat $SOFTCLICKER_HOME/softclicker.pid`
  exit 0
elif [ "$CMD" = "restart" ]; then
  export SOFTCLICKER_HOME=$SOFTCLICKER_HOME
  kill -term `cat $SOFTCLICKER_HOME/softclicker.pid`
  process_status=0
  pid=`cat $SOFTCLICKER_HOME/softclicker.pid`
  while [ "$process_status" -eq "0" ]
  do
        sleep 1;
        ps -p$pid 2>&1 > /dev/null
        process_status=$?
  done

# using nohup sh to avoid erros in solaris OS.TODO
  nohup sh $SOFTCLICKER_HOME/bin/softclicker.sh $args > /dev/null 2>&1 &
  exit 0
elif [ "$CMD" = "test" ]; then
    JAVACMD="exec "$JAVACMD""
elif [ "$CMD" = "version" ]; then
  cat $SOFTCLICKER_HOME/bin/version.txt
  exit 0
fi

# ---------- Handle the SSL Issue with proper JDK version --------------------
jdk_17=`$JAVA_HOME/bin/java -version 2>&1 | grep "1.[7|8]"`
if [ "$jdk_17" = "" ]; then
   echo " Starting SoftClicker (in unsupported JDK)"
   echo " [ERROR] SoftClicker is supported only on JDK 1.7 and 1.8"
fi

SOFTCLICKER_XBOOTCLASSPATH=""
for f in "$SOFTCLICKER_HOME"/lib/xboot/*.jar
do
    if [ "$f" != "$SOFTCLICKER_HOME/lib/xboot/*.jar" ];then
        SOFTCLICKER_XBOOTCLASSPATH="$SOFTCLICKER_XBOOTCLASSPATH":$f
    fi
done

JAVA_ENDORSED_DIRS="$SOFTCLICKER_HOME/lib/endorsed":"$JAVA_HOME/jre/lib/endorsed":"$JAVA_HOME/lib/endorsed"

SOFTCLICKER_CLASSPATH=""
if [ -e "$JAVA_HOME/lib/tools.jar" ]; then
    SOFTCLICKER_CLASSPATH="$JAVA_HOME/lib/tools.jar"
fi
for f in "$SOFTCLICKER_HOME"/bin/*.jar
do
    if [ "$f" != "$SOFTCLICKER_HOME/bin/*.jar" ];then
        SOFTCLICKER_CLASSPATH="$SOFTCLICKER_CLASSPATH":$f
    fi
done
for t in "$SOFTCLICKER_HOME"/lib/*.jar
do
    SOFTCLICKER_CLASSPATH="$SOFTCLICKER_CLASSPATH":$t
done

# For Cygwin, switch paths to Windows format before running java
if $cygwin; then
  JAVA_HOME=`cygpath --absolute --windows "$JAVA_HOME"`
  SOFTCLICKER_HOME=`cygpath --absolute --windows "$SOFTCLICKER_HOME"`
  CLASSPATH=`cygpath --path --windows "$CLASSPATH"`
  JAVA_ENDORSED_DIRS=`cygpath --path --windows "$JAVA_ENDORSED_DIRS"`
  SOFTCLICKER_CLASSPATH=`cygpath --path --windows "$SOFTCLICKER_CLASSPATH"`
  SOFTCLICKER_XBOOTCLASSPATH=`cygpath --path --windows "$SOFTCLICKER_XBOOTCLASSPATH"`
fi

# ----- Adding Java FX support ------------------------------------------------
SOFTCLICKER_CLASSPATH="$SOFTCLICKER_CLASSPATH":"$JAVA_HOME"/jre/lib/jfxrt.jar

# ----- Execute The Requested Command -----------------------------------------

echo JAVA_HOME environment variable is set to $JAVA_HOME
echo SOFTCLICKER_HOME environment variable is set to $SOFTCLICKER_HOME
cd "$SOFTCLICKER_HOME"

TMP_DIR=$SOFTCLICKER_HOME/tmp
if [ -d "$TMP_DIR" ]; then
rm -rf "$TMP_DIR"
fi

START_EXIT_STATUS=121
status=$START_EXIT_STATUS

while [ "$status" = "$START_EXIT_STATUS" ]
do
    $JAVACMD \
    -Xbootclasspath/a:"$SOFTCLICKER_XBOOTCLASSPATH" \
    -XX:+HeapDumpOnOutOfMemoryError \
    -XX:HeapDumpPath="$SOFTCLICKER_HOME/repository/logs/heap-dump.hprof" \
    $JAVA_OPTS \
    -classpath "$SOFTCLICKER_CLASSPATH" \
    -Djava.endorsed.dirs="$JAVA_ENDORSED_DIRS" \
    -Djava.io.tmpdir="$SOFTCLICKER_HOME/tmp" \
    -Dlog4j.configuration="file:$SOFTCLICKER_HOME/conf/log4j.properties" \
    -Djava.command="$JAVACMD" \
    -Dsoftclicker.home="$SOFTCLICKER_HOME" \
    -Ddisplay.agent.home="$SOFTCLICKER_HOME" \
    -Djava.security.egd=file:/dev/./urandom \
    -Dfile.encoding=UTF8 \
    -Djava.net.preferIPv4Stack=true \
    org.softclicker.server.starup.Bootstrap $*
    status=$?
done