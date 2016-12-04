#!/bin/bash -e

# Prevent owner issues on mounted folders
chown -R oracle:dba /u01/app/oracle
rm -f /u01/app/oracle/product
ln -s /u01/app/oracle-product /u01/app/oracle/product

#Run Oracle root scripts
/u01/app/oraInventory/orainstRoot.sh > /dev/null 2>&1
echo | /u01/app/oracle/product/12.1.0/xe/root.sh > /dev/null 2>&1

echo "Initializing database."
export IMPORT_FROM_VOLUME=true

if [ -z "$CHARACTER_SET" ]; then
  export CHARACTER_SET="AL32UTF8"
fi

#printf "Setting up:\nprocesses=$processes\nsessions=$sessions\ntransactions=$transactions\n"

mv /u01/app/oracle-product/12.1.0/xe/dbs /u01/app/oracle/dbs
ln -s /u01/app/oracle/dbs /u01/app/oracle-product/12.1.0/xe/dbs

echo "Starting tnslsnr"
su oracle -c "/u01/app/oracle/product/12.1.0/xe/bin/tnslsnr &"
#create DB for SID: xe
su oracle -c "$ORACLE_HOME/bin/dbca -silent -createDatabase -templateName General_Purpose.dbc -gdbname xe.oracle.docker -sid xe -responseFile NO_VALUE -characterSet $CHARACTER_SET -totalMemory $DBCA_TOTAL_MEMORY -emConfiguration LOCAL -pdbAdminPassword oracle -sysPassword oracle -systemPassword oracle"

echo "Configuring Apex console"
cd $ORACLE_HOME/apex
su oracle -c 'echo -e "0Racle$\n8080" | $ORACLE_HOME/bin/sqlplus -S / as sysdba @apxconf > /dev/null'
su oracle -c 'echo -e "${ORACLE_HOME}\n\n" | $ORACLE_HOME/bin/sqlplus -S / as sysdba @apex_epg_config_core.sql > /dev/null'
su oracle -c 'echo -e "ALTER USER ANONYMOUS ACCOUNT UNLOCK;" | $ORACLE_HOME/bin/sqlplus -S / as sysdba > /dev/null'

echo "Starting import from '/docker-entrypoint-initdb.d':"

for f in /docker-entrypoint-initdb.d/*; do
  echo "found file /docker-entrypoint-initdb.d/$f"
  case "$f" in
    *.sh)     echo "[IMPORT] $0: running $f"; . "$f" ;;
    *.sql)    echo "[IMPORT] $0: running $f"; echo "exit" | su oracle -c "NLS_LANG=.$CHARACTER_SET /u01/app/oracle/product/12.1.0/xe/bin/sqlplus -S / as sysdba @$f"; echo ;;
    *)        echo "[IMPORT] $0: ignoring $f" ;;
  esac
  echo
done

echo "Import finished"
echo

echo "Database ready to use. Enjoy! ;)"
touch /tmp/oracle.is.ready