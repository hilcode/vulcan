#!/bin/bash

ROOT_DIR=$(dirname $0)
cd $ROOT_DIR

LIBS=(0 1 2 3 4 5 6 7 8 9)
SUBLIBS=(0 1 2 3 4 5 6 7 8 9)
SUBSUBLIBS=(0 1 2 3 4 5 6 7 8 9)

function create_project {
	local index=$1
	printf 'Creating project lib_%s ... ' ${index}
	mkdir -p $ROOT_DIR/project/lib_${index}
	cd $ROOT_DIR/project/lib_${index}
	cp -a $ROOT_DIR/commons-lang3/. .
	find src/org/apache/commons/lang3 -type f -name '*.java' -print | xargs sed -i -e "s/ org.apache.commons.lang3\([;.]\)/ org.apache.lib_${index}\1/g"
	mv src/org/apache/commons/lang3 src/org/apache/lib_${index}
	rmdir src/org/apache/commons
	$ROOT_DIR/create-lib-build.ninja.sh ${index} ${SUBLIBS[@]}
	printf 'done\n'
}

function create_subproject {
	local index=$1
	local subindex=$2
	printf 'Creating subproject lib_%s/sublib_%s ... ' ${index} ${subindex}
	mkdir -p $ROOT_DIR/project/lib_${index}/sublib_${subindex}
	cd $ROOT_DIR/project/lib_${index}/sublib_${subindex}
	cp -a $ROOT_DIR/commons-lang3/. .
	find src/org/apache/commons/lang3 -type f -name '*.java' -print | xargs sed -i -e "s/ org.apache.commons.lang3\([;.]\)/ org.apache.lib_${index}.sublib_${subindex}\1/g"
	mkdir -p src/org/apache/lib_${index}
	mv src/org/apache/commons/lang3 src/org/apache/lib_${index}/sublib_${subindex}
	rmdir src/org/apache/commons
	$ROOT_DIR/create-sublib-build.ninja.sh ${index} ${subindex} ${SUBSUBLIBS[@]}
	printf 'done\n'
}

function create_subsubproject {
	local index=$1
	local subindex=$2
	local subsubindex=$3
	printf 'Creating subsubproject lib_%s/sublib_%s/subsublib_%s ... ' ${index} ${subindex} ${subsubindex}
	mkdir -p $ROOT_DIR/project/lib_${index}/sublib_${subindex}/subsublib_${subsubindex}
	cd $ROOT_DIR/project/lib_${index}/sublib_${subindex}/subsublib_${subsubindex}
	cp -a $ROOT_DIR/commons-lang3/. .
	find src/org/apache/commons/lang3 -type f -name '*.java' -print | xargs sed -i -e "s/ org.apache.commons.lang3\([;.]\)/ org.apache.lib_${index}.sublib_${subindex}.subsublib_${subsubindex}\1/g"
	mkdir -p src/org/apache/lib_${index}/sublib_${subindex}
	mv src/org/apache/commons/lang3 src/org/apache/lib_${index}/sublib_${subindex}/subsublib_${subsubindex}
	rmdir src/org/apache/commons
	$ROOT_DIR/create-subsublib-build.ninja.sh ${index} ${subindex} ${subsubindex}
	printf 'done\n'
}

function create_main {
	mkdir -p $ROOT_DIR/project/src/org/apache
	cat >>$ROOT_DIR/project/src/org/apache/Main.java <<-EOF
		package org.apache;

		public class Main {

		    public static final void main(String... args) {
	EOF
	for index in ${LIBS[@]}; do
		echo "        System.out.println(org.apache.lib_${index}.builder.Builder.class);" >>$ROOT_DIR/project/src/org/apache/Main.java
	done
	cat >>$ROOT_DIR/project/src/org/apache/Main.java <<-EOF
		    }

		}
	EOF
}

function main {
	rm -rf $ROOT_DIR/project
	mkdir -p $ROOT_DIR/project
	create_main
	cat >>$ROOT_DIR/project/build.ninja <<-EOF
		class_path = ""

		rule javac
		    command = javac --source-path src --class-path \$class_path -d \$target_dir -source 11 @\$source_files

		rule create-jar
		    command = jar --create --file \$out -C \$source_dir .

		rule create-directory
		    command = mkdir -p \$out

		rule create-source-files
		    command = echo \$in >\$out

		build $
		    .vulcan/main/source-files.lst \$
		    : create-source-files \$
		        src/org/apache/Main.java

		build .vulcan/main/classes/org/apache/Main.class \$
		    : javac \$
	EOF
	for index in ${LIBS[@]}; do
		echo "            .vulcan/lib_${index}/assembly/lib_${index}.jar \$" >>$ROOT_DIR/project/build.ninja
	done
	cat >>$ROOT_DIR/project/build.ninja <<-EOF
		            .vulcan/main/source-files.lst
		        source_files = .vulcan/main/source-files.lst
		        target_dir = .vulcan/main/classes
		        class_path = \$
	EOF
	for index in ${LIBS[@]}; do
		echo "            :.vulcan/lib_${index}/classes\$" >>$ROOT_DIR/project/build.ninja
	done
	cat >>$ROOT_DIR/project/build.ninja <<-EOF

		build .vulcan/main/assembly: create-directory

		build .vulcan/main/classes: create-directory

	EOF
	for index in ${LIBS[@]}; do
		create_project ${index}
		echo "subninja lib_${index}/build.ninja" >>$ROOT_DIR/project/build.ninja
		for subindex in ${SUBLIBS[@]}; do
			create_subproject ${index} ${subindex}
			for subsubindex in ${SUBSUBLIBS[@]}; do
				create_subsubproject ${index} ${subindex} ${subsubindex}
			done
		done
	done
}

main "$@"
