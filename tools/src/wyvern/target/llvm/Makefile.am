AUTOMAKE_OPTIONS = subdir-objects
ACLOCAL_AMFLAGS = ${ACLOCAL_FLAGS}
AM_CPPFLAGS = $(DEPS_CFLAGS)

lib_LTLIBRARIES = libWyvernLLVM.la
libWyvernLLVM_la_SOURCES = src/WyvernClass.h \
src/WyvernClass.cpp \
src/WyvernGlobals.h \
src/WyvernGlobals.cpp \
src/WyvernFunction.h \
src/WyvernFunction.cpp \
src/oirEmitLLVMNative.cpp \
src/wyvern_target_oir_EmitLLVMNative.h

libWyvernLLVM_la_CXXFLAGS = -Wall -std=c++11 `llvm-config --cxxflags --ldflags --system-libs --libs mcjit jit native core`
libWyvernLLVM_la_LIBADD = $(DEPS_LIBS)
libWyvernLLVM_la_LDFLAGS = -module -avoid-version -shared
#-I/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.40-25.b25.fc21.x86_64/include -I/usr/lib/jvm/java-1.8.0-openjdk-1.8.0.40-25.b25.fc21.x86_64/include/linux
dist_noinst_SCRIPTS = autogen.sh
