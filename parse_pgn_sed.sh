#!/bin/sh

if [ $# -ne 1 ]; then
	echo 'Please give the pgn file as the only argument.'
	exit
fi

sed -n '
# remove carriage return
s/\r//g
# extract the results, i.e. 0, 1/2, or 1
s/^\[Result "\(.\+\)\-.\+\]$/\1/
# convert result 1 to 2
s/^1$/2/p
# convert result 1/2 to 1
s/^1\/2$/1/p
/^0$/p
# print elo ratings
s/^\[\(White\|Black\)Elo "\([0-9]\+\)"\]$/\2/p
' $1 | sed -n '
# convert all sequences of lines consisting of result\nelo\nelo into result,elo,elo
/^[012]$/ {
	N
	N
	s/\([012]\)\n\([0-9]\{3,4\}\)\n\([0-9]\{3,4\}\)/\1,\2,\3/p
}
'
