HOME_DIRECTORY=`pwd`

if [ -f "alt-category.zip" ]; then
	rm -rf "alt-category"
	unzip -q "alt-category.zip" -d "alt-category"
	rm -rf "alt-category.zip"
	cd "alt-category"
	mvn clean install
	cd "$HOME_DIRECTORY"
fi

# 8GB
#mvn exec:java -Dexec.mainClass=ai.alt.category.MAIN -Dexec.args="/home/thoq/wikidatawiki-20160601-pages-articles.xml.bz2"


# 2GB
#mvn exec:java -Dexec.mainClass=ai.alt.category.MAIN -Dexec.args="/home/thoq/wikidatawiki-20160601-pages-articles3.xml-p007305527p016090523.bz2"

