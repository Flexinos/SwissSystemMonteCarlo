#!/bin/sh

TMP_DIR=intermediary_result_storage
TMP_FILE_BASE=thread
THREAD_SCRIPT="./parse_partial_pgn.sh"

if [ $# -ne 3 ]; then
	echo "Usage: $0 num_threads input_file output_file"
	exit
fi

if [ -d $TMP_DIR ]; then
	echo "Tried to create the temporary folder \"$TMP_DIR\", but it already exists. Please rename the directory or this script's variable TMP_DIR."
	exit
fi

set -e

mkdir $TMP_DIR

LINE_COUNT=$(wc -l $2 | awk '{print $1}')
STARTLINE=1
LINE_INCREMENT=$(expr $LINE_COUNT / $1)
ITERATION=1
while [ $ITERATION -lt $1 ]; do
	$THREAD_SCRIPT $STARTLINE $(expr $STARTLINE + $LINE_INCREMENT) $2 $TMP_DIR/$TMP_FILE_BASE""$ITERATION  &
	STARTLINE=$(expr $STARTLINE + $LINE_INCREMENT)
	ITERATION=$(expr $ITERATION + 1)
done
$THREAD_SCRIPT $STARTLINE $LINE_COUNT $2 $TMP_DIR/$TMP_FILE_BASE""$ITERATION &
wait

cat $TMP_DIR/* > $3

rm -r $TMP_DIR
