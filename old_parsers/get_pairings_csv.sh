#!/bin/sh

# run the following line to install required dependency
# pip install csvkit

# enter the link in double quotes to prevent the shell from interpreting it

if [ "$#" -ne 1 ]; then
	echo "Please specify the link to the xlsx file."
	exit
fi

curl $1 2>/dev/null | in2csv -f xlsx 2>/dev/null | sed '/nicht ausgelost/d' | awk -F "," '/^[0-9]+,.*$/ {print $1 "," $2 "," $NF}'
