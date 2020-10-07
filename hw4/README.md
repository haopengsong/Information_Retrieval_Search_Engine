Instructions for executing the UI:
1. Start the Solr server and make sure it runs on localhost:8983. If not, please change the port number and/or ip address on phpclient.php at line 20

2. Since the external_pageRankFile.txt was indexed on my laptop as it configured each id starts with the directory path on my laptop, you have to replace the path to be where you put the downloaded webpages on you laptop. So '/Users/haopengsong/572/solr-7.7.0/../' needs to be changed to 'your/file/path/....'.

3. Also to be able to fetch a missing URL from the map file, change $dirPath at line 104 of phpclient.php to the path where you put the downloaded webpages files.