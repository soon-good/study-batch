# postgresql docker container repl 접속 스크립트

name_local_postgresql='local-postgresql'

cnt_local_postgresql=`docker container ls --filter name=local-postgresql | wc -l`
cnt_local_postgresql=$(($cnt_local_postgresql -1))

if [ $cnt_local_postgresql -eq 0 ]
then
    echo "'$name_local_postgresql' 컨테이너가 없습니다. 컨테이너를 구동해주세요."

else
    echo "'$name_local_postgresql' 컨테이너의 BASH 쉘 접속을 시작합니다."
    docker container exec -it local-postgresql sh
fi
