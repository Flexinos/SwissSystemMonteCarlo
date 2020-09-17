#!/bin/sh

# pgn file must be specified as the only argument
if [ $# -ne 1 ]; then
	echo "Please specify the pgn file"
	exit
fi

# sed removes carriage returns (in case the file comes from Windows)
# first awk script extracts result, white elo, black elo and puts them into csv format
# second awk script removes lines which do not match the desired format specified in the regex:
# result,white elo,black elo\n
sed 's/\r//g' $1 | awk -f pgn_parser.awk | awk '/^[01](\.5)?,[0-9]{3,4},[0-9]{3,4}$/ {print $0}'
