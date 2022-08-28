#!/bin/bash

ROOT_DIR=$(dirname $0)
cd $ROOT_DIR

function main {
	local index=$1
	shift
	local subindex=$1
	shift
	local subsubindices=("$@")
	cd project
	BUILD_NINJA=$ROOT_DIR/project/lib_${index}/sublib_${subindex}/build.ninja
	rm -f $BUILD_NINJA
	for subsubindex in ${subsubindices[@]}; do
		echo "subninja lib_${index}/sublib_${subindex}/subsublib_${subsubindex}/build.ninja" >>$BUILD_NINJA
	done
	cat >>$BUILD_NINJA <<-EOF
	build \$
	    .vulcan/lib_${index}/sublib_${subindex}/source-files.lst \$
	    : create-source-files \$
	EOF
	for input_file in $(find lib_${index}/sublib_${subindex}/src -type f -name '*.java' -print); do
		echo "        $input_file \$" >>$BUILD_NINJA
	done
	cat >>$BUILD_NINJA <<-EOF

	build \$
	EOF
	mkdir -p /tmp/$$/classes
	find lib_${index}/sublib_${subindex}/src -type f -name '*.java' -print >/tmp/$$/source-files.lst
	javac --source-path src -d /tmp/$$/classes -cp /tmp/$$/classes -source 11 @/tmp/$$/source-files.lst
	(
		cd /tmp/$$
		for output_file in $(find classes/ -type f -name '*.class' -print); do
			echo "    .vulcan/lib_${index}/sublib_${subindex}/${output_file//\$/\$\$} \$" >>$BUILD_NINJA
		done
	)
	cat >>$BUILD_NINJA <<-EOF
	    : javac \$
	EOF
	for subsubindex in ${subsubindices[@]}; do
		echo "            .vulcan/lib_${index}/sublib_${subindex}/subsublib_${subsubindex}/assembly/lib_${index}_${subindex}_${subsubindex}.jar \$" >>$BUILD_NINJA
	done
	cat >>$BUILD_NINJA <<-EOF
	            .vulcan/lib_${index}/sublib_${subindex}/source-files.lst
	        source_files = .vulcan/lib_${index}/sublib_${subindex}/source-files.lst
	        target_dir = .vulcan/lib_${index}/sublib_${subindex}/classes

	build \$
	    .vulcan/lib_${index}/sublib_${subindex}/assembly/lib_${index}_${subindex}.jar \$
	    : create-jar \$
	EOF
	(
		cd /tmp/$$
		for output_file in $(find classes/ -type f -name '*.class' -print); do
			echo "        .vulcan/lib_${index}/sublib_${subindex}/${output_file//\$/\$\$} \$" >>$BUILD_NINJA
		done
	)
	cat >>$BUILD_NINJA <<-EOF
	        .vulcan/lib_${index}/sublib_${subindex}/classes
	    source_dir = .vulcan/lib_${index}/sublib_${subindex}/classes

	build .vulcan/lib_${index}/sublib_${subindex}/assembly: create-directory

	build .vulcan/lib_${index}/sublib_${subindex}/classes: create-directory
	EOF
	rm -rf /tmp/$$
}

main "$@"
