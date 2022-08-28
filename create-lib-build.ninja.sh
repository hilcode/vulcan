#!/bin/bash

ROOT_DIR=$(dirname $0)
cd $ROOT_DIR

function main {
	local index=$1
	shift
	local subindices=("$@")
	cd project
	BUILD_NINJA=$ROOT_DIR/project/lib_${index}/build.ninja
	rm -f $BUILD_NINJA
	for subindex in ${subindices[@]}; do
		echo "subninja lib_${index}/sublib_${subindex}/build.ninja" >>$BUILD_NINJA
	done
	cat >>$BUILD_NINJA <<-EOF

	build \$
	    .vulcan/lib_${index}/source-files.lst \$
	    : create-source-files \$
	EOF
	for input_file in $(find lib_${index}/src -type f -name '*.java' -print); do
		echo "        $input_file \$" >>$BUILD_NINJA
	done
	cat >>$BUILD_NINJA <<-EOF

	build \$
	EOF
	mkdir -p /tmp/$$/classes
	find lib_${index}/src -type f -name '*.java' -print >/tmp/$$/source-files.lst
	javac --source-path src -d /tmp/$$/classes -cp /tmp/$$/classes -source 11 @/tmp/$$/source-files.lst
	(
		cd /tmp/$$
		for output_file in $(find classes/ -type f -name '*.class' -print); do
			echo "    .vulcan/lib_${index}/${output_file//\$/\$\$} \$" >>$BUILD_NINJA
		done
	)
	cat >>$BUILD_NINJA <<-EOF
	    : javac \$
	EOF
	for subindex in ${subindices[@]}; do
		echo "            .vulcan/lib_${index}/sublib_${subindex}/assembly/lib_${index}_${subindex}.jar \$" >>$BUILD_NINJA
	done
	cat >>$BUILD_NINJA <<-EOF
	            .vulcan/lib_${index}/source-files.lst
	        source_files = .vulcan/lib_${index}/source-files.lst
	        target_dir = .vulcan/lib_${index}/classes

	build \$
	    .vulcan/lib_${index}/assembly/lib_${index}.jar \$
	    : create-jar \$
	EOF
	(
		cd /tmp/$$
		for output_file in $(find classes/ -type f -name '*.class' -print); do
			echo "        .vulcan/lib_${index}/${output_file//\$/\$\$} \$" >>$BUILD_NINJA
		done
	)
	cat >>$BUILD_NINJA <<-EOF
	        .vulcan/lib_${index}/classes
	    source_dir = .vulcan/lib_${index}/classes

	build .vulcan/lib_${index}/assembly: create-directory

	build .vulcan/lib_${index}/classes: create-directory
	EOF
	rm -rf /tmp/$$
}

main "$@"
