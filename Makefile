all: 
	lein uberjar
	native-image --enable-url-protocols=https --initialize-at-build-time -jar target/uberjar/bigpfetch-0.1.0-SNAPSHOT-standalone.jar
	mv bigpfetch-0.1.0-SNAPSHOT-standalone flakemake
	
