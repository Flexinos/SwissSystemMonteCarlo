#!/bin/sh

# remove \r from regexes in first awk call if the file does not contain carriage returns

if [ $# -ne 4 ]; then
	echo "Usage: $0 startline endline infile outfile"
	exit
fi

sed -nE '
1,'"$1"' d
'"$2"' q
# Quit if line does not contain valid result
/^\[Result "[01]/ !d
# put result line into hold buffer
h
# put next line into pattern space
n
# skip line if it contains the regex (might need improvement)
/^\[ECO/ n
# stop matching if next line is not WhiteElo
/^\[WhiteElo "([89][0-9]{2}|[12][0-9]{3})/ !d
# append white elo line into hold buffer
H
# put next line into pattern space
n
# stop matching if it is not BlackElo
/^\[BlackElo "([89][0-9]{2}|[12][0-9]{3})/ !d
# append black elo line to hold buffer
H
# put hold buffer content into pattern space
g
# extract relevant data
s/^\[Result "(0|1|1\/2)\-(0|1|1\/2)"\]\r\n\[WhiteElo "([0-9]{3,4})"\]\r\n\[BlackElo "([0-9]{3,4})/\1,\3,\4/
# convert result 1 to 2
s/^1,/2,/
# convert result 1/2 into 1
s/^1\/2/1/
# print resulting csv line
p
' $3 > $4
