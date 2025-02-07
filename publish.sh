#!/bin/bash


function failed() {
    echo "Build failed! reason: [$1]"
    exit 1
}


## 获取版本号
VERSION=$(./gradlew properties -q | grep '^version:' | awk '{print $2}')
if [ "$VERSION" ]; then
  echo "version is [$VERSION]"
else
  failed "version is empty"
fi


## 版本号校验（SNAPSHOT版本）
#### 定义正则表达式 (注意：在 bash 中用 [[ ]] 支持正则匹配)
VERSION_REGEX="^[0-9]+\.[0-9]+\.[0-9]+(-SNAPSHOT)?$"
if [[ ! "$VERSION" =~ $VERSION_REGEX ]]; then
  failed "version 格式不正确！正确格式应为: x.x.x[-SNAPSHOT]"
fi
#### 去除后缀
if [[ "$VERSION" == *"-SNAPSHOT" ]]; then
  VERSION=${VERSION%-SNAPSHOT}
fi
#### 添加后缀
VERSION_SNAPSHOT=$VERSION-SNAPSHOT
VERSION_RELEASE=$VERSION


## 根据CI环境类型，确定要往哪里发布
if [[ "$CI_PIPELINE_SOURCE" == "merge_request_event" ]]; then
  echo "CI_PIPELINE_SOURCE is [$CI_PIPELINE_SOURCE]"
  echo "publish SNAPSHOT"
  ./gradlew  -Pbuild.publish.version=$VERSION_SNAPSHOT publish
elif [[ "$CI_PIPELINE_SOURCE" == "push" && "$CI_COMMIT_BRANCH" == "$CI_DEFAULT_BRANCH" ]]; then
  echo "publish RELEASE"
  ./gradlew  -Pbuild.publish.version=$VERSION_RELEASE publish
else
  echo "Didn't publish anything"
fi
# 测试不发布
##./gradlew -Pbuild.publish.version=$VERSION_SNAPSHOT properties | grep '^version:'
#./gradlew  -Pbuild.publish.version=$VERSION_SNAPSHOT publish