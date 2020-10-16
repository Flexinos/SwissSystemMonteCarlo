#!/bin/sh

SPLIT_FILES_DIR=tmp_split_files
PARTIAL_RESULTS_DIR=tmp_partial_results
PARSER_SCRIPT="./parse_pgn.sh"

if [ $# -ne 3 ]; then
	echo "Usage: $0 num_threads input_file output_file"
	exit
fi

if [ -d $SPLIT_FILES_DIR ]; then
	echo "Directory $SPLIT_FILES_DIR already exists. Please rename it or the SPLIT_FILES_DIR variable in this script."
	exit
fi

if [ -d $PARTIAL_RESULTS_DIR ]; then
	echo "Directory $PARTIAL_RESULTS_DIR already exists. Please rename it or the PARTIAL_RESULTS_DIR variable in this script."
	exit
fi

set -e

mkdir $SPLIT_FILES_DIR
mkdir $PARTIAL_RESULTS_DIR
cd $SPLIT_FILES_DIR
split --number=l/$1 $2
cd ..
for partial_file in $SPLIT_FILES_DIR/*; do
	$PARSER_SCRIPT $partial_file > $PARTIAL_RESULTS_DIR/$(basename $partial_file) &
done
wait
cat $PARTIAL_RESULTS_DIR/* > $3
rm -r $SPLIT_FILES_DIR $PARTIAL_RESULTS_DIR
