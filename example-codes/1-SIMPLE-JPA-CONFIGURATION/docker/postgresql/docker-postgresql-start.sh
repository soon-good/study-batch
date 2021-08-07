# postgresql docker container 구동 스크립트

name_local_postgresql='local-postgresql'
cnt_local_postgresql=`docker container ls --filter name=local-postgresql | wc -l`
cnt_local_postgresql=$(($cnt_local_postgresql -1))

if [ $cnt_local_postgresql -eq 0 ]
then
    echo "'$name_local_postgresql' 컨테이너를 구동시킵니다.\n"

    # 디렉터리 존재 여부 체크 후 없으면 새로 생성
    DIRECTORY=~$USER/env/docker/postgresql/volumes/local-postgresql
    test -f $DIRECTORY && echo "볼륨 디렉터리가 존재하지 않으므로 새로 생성합니다.\n"

    if [ $? -lt 1 ]; then
      mkdir -p ~$USER/env/docker/postgresql/volumes/local-postgresql
    fi

    # postgresql 컨테이너 구동 & 볼륨 마운트
    docker container run --rm -d -p 35432:5432 --name local-postgresql \
                -v ~/env/docker/postgresql/volumes/local-postgresql:/var/lib/postgresql/data \
                -e POSTGRES_DB=postgres \
                -e POSTGRES_USER=postgres \
                -e POSTGRES_PASSWORD=1111 \
                -d postgres:11-alpine

else
    echo "'$name_local_postgresql' 컨테이너가 존재합니다. 기존 컨테이너를 중지하고 삭제합니다."
    # 컨테이너 중지 & 삭제
    docker container stop local-postgresql

    # 컨테이너 볼륨 삭제
    rm -rf ~/env/docker/postgresql/volumes/local-postgresql/*
    echo "\n'$name_local_postgresql' 컨테이너 삭제를 완료했습니다.\n"

    # 디렉터리 존재 여부 체크 후 없으면 새로 생성
    DIRECTORY=~$USER/env/docker/postgresql/volumes/local-postgresql
    test -f $DIRECTORY && echo "볼륨 디렉터리가 존재하지 않으므로 새로 생성합니다.\n"

    if [ $? -lt 1 ]; then
      mkdir -p ~$USER/env/docker/postgresql/volumes/local-postgresql
    fi

    # postgresql 컨테이너 구동 & 볼륨 마운트
    echo "'$name_local_postgresql' 컨테이너를 구동시킵니다."
    docker container run --rm -d -p 35432:5432 --name local-postgresql \
                -v ~/env/docker/postgresql/volumes/local-postgresql:/var/lib/postgresql/data \
                -e POSTGRES_DB=postgres \
                -e POSTGRES_USER=postgres \
                -e POSTGRES_PASSWORD=1111 \
                -d postgres:11-alpine
fi
