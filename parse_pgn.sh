#!/bin/sh

if [ $# -ne 1 ]; then
	echo "Please specify the pgn file"
	exit
fi

awk -f pgn_parser.awk $1 | sed 's/\r//g'
