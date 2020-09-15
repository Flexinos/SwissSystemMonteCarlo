#!/bin/sh

if [ $# -ne 1 ]; then
	echo "Please specify the pgn file"
	exit
fi

sed 's/\r//g' $1 | awk -f pgn_parser.awk #| awk '/^[01](\.5)?,[0-9]{3,4},[0-9]{3,4}/ {print $0}'
