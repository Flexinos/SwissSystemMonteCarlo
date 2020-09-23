#!/bin/sh

# run the following line to install required dependency
# pip install csvkit

# enter the link in double quotes to prevent the shell from interpreting it
# The awk script relies on the column names being German, so use the lan=0 flag in the link.

if [ "$#" -ne 1 ]; then
        echo "Please specify the link to the xlsx file."
        exit
fi

curl $1 2>/dev/null | \
in2csv -f xlsx 2>/dev/null | \
awk -f parse_scores_csv.awk | \
sed 's/ ,/,/g'
