#!/bin/bash

echo "Waiting for MySQL to be ready..."
until mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" -e "SELECT 1" &> /dev/null
do
  echo "MySQL is unavailable - sleeping"
  sleep 3
done

echo "MySQL is up - initializing database"
mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASS" "$DB_NAME" < /docker-entrypoint-initdb.d/init.sql

echo "Database initialized - starting Tomcat"
exec catalina.sh run
