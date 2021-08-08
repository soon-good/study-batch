# postgresql docker container 중지 및 볼륨 삭제 스크립트

name_local_postgresql='local-postgresql'

cnt_local_postgresql=`docker container ls --filter name=local-postgresql | wc -l`
cnt_local_postgresql=$(($cnt_local_postgresql -1))

if [ $cnt_local_postgresql -eq 0 ]
then
    echo "'$name_local_postgresql' 컨테이너가 없습니다. 삭제를 진행하지 않습니다."

else
    echo "'$name_local_postgresql' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    docker container stop local-postgresql
    rm -rf ~/env/docker/postgresql/volumes/local-postgresql/*
    echo "\n'$name_local_postgresql' 컨테이너 삭제를 완료했습니다.\n"
fi
