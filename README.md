# spring-boot-solace

Monitor all topics starts from ""st/g/...:
./sdkperf_java.sh -cip 10.194.117.223 -cu tempuser@csp_poc -cp 00000000 -stl "st/g/>" -md

Monitor sts/translation/pending queue
./sdkperf_java.sh -cip 10.194.117.223 -cu tempuser@csp_poc -cp 00000000 -sql  sts/translation/pending -md

push messageto queueu
./sdkperf_java.sh -cip 10.194.117.223 -cu tempuser@csp_poc -cp 00000000 -pql sms/sessioncheckpending -mn 1 -mr 1 -md -pfl ~/Documents/temp.txt
