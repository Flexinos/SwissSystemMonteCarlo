BEGIN {FS=","}
/^Rg\.,Snr,.*$/ { 
	for (i = 1; i <= NF; ++i) {
		if ($i == "Rg.") RG=i
		else if ($i == "Snr") SNR=i
		else if ($i == "Name") NAME=i
		else if ($i == "EloI") ELOI=i
		else if ($i == "EloN") ELON=i
		else if ($i == "Pkt. ") PKT=i
		else if ($i == "Wtg1") WTG1=i
		else if ($i == "Wtg2") WTG2=i
		else if ($i == "Wtg3") WTG3=i
	}
}
/^[0-9]+,.*$/ {print $RG "," $SNR "," $NAME "," $ELOI "," $ELON "," $PKT "," $WTG1 "," $WTG2 "," $WTG3}
