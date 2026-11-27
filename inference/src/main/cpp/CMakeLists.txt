# SPDX-License-Identifier: MIT
# Copyright (c) 2026 Aman Kumar. All rights reserved.
# See LICENSE-MIT in the project root for license terms.

cmake_minimum_required(VERSION 3.22.1)
project("locian_inference")

add_subdirectory(../../../../external/llama.cpp llama.cpp)
add_subdirectory(../../../../external/cpu_features cpu_features)

set(LLAMA_DIR_RELATIVE "../../../../external/llama.cpp")
get_filename_component(LLAMA_DIR ${LLAMA_DIR_RELATIVE} ABSOLUTE)

set(GGML_DIR ${LLAMA_DIR}/ggml)
set(COMMON_DIR ${LLAMA_DIR}/common)
set(VENDOR_DIR ${LLAMA_DIR}/vendor)

target_compile_options(
    llama
    PRIVATE
    -fvisibility=hidden -fvisibility-inlines-hidden
)
target_compile_options(
    llama
    PUBLIC
    -ffunction-sections -fdata-sections
)
target_link_options(
    llama
    PRIVATE
    -Wl,--gc-sections -flto
    -Wl,--exclude-libs,ALL
)

function(build_library target_name)
    add_library(
        ${target_name}
        SHARED
        LLMInference.cpp
        inference_jni.cpp
    )
    target_include_directories(
        ${target_name}
        PUBLIC
        ${COMMON_DIR}
        ${GGML_DIR}/include
        ${GGML_DIR}/src
        ${GGML_DIR}/src/ggml-cpu
        ${LLAMA_DIR}/include
        ${VENDOR_DIR}
    )
    target_compile_options(
        ${target_name}
        PUBLIC
        -fvisibility=hidden -fvisibility-inlines-hidden
        -ffunction-sections -fdata-sections
    )
    target_link_libraries(
        ${target_name}
        android log llama llama-common
    )
    target_link_options(
        ${target_name}
        PRIVATE
        -Wl,--gc-sections -flto
        -Wl,--exclude-libs,ALL
    )
endfunction()

function(build_library_arm64 target_name cpu_flags)
    build_library(${target_name})
    set(GGML_SYSTEM_ARCH "ARM")
    target_compile_definitions(${target_name} PRIVATE
        GGML_SYSTEM_ARCH=${GGML_SYSTEM_ARCH}
    )
    target_compile_options(
        ${target_name}
        PUBLIC
        -DGGML_USE_CPU -DGGML_USE_CPU_AARCH64 ${cpu_flags} -O3
    )
endfunction()

function(build_library_universal target_name)
    build_library(${target_name})
    target_compile_options(
        ${target_name}
        PUBLIC
        -DGGML_USE_CPU -O3
    )
endfunction()

build_library_universal("locian_inference")
if (${ANDROID_ABI} STREQUAL "arm64-v8a")
    build_library_arm64("locian_inference_v8" "-march=armv8-a")
    build_library_arm64("locian_inference_v8_2_fp16" "-march=armv8.2-a+fp16")
    build_library_arm64("locian_inference_v8_2_fp16_dotprod" "-march=armv8.2-a+fp16+dotprod")
    build_library_arm64("locian_inference_v8_4_fp16_dotprod" "-march=armv8.4-a+fp16+dotprod")
    build_library_arm64("locian_inference_v8_4_fp16_dotprod_i8mm" "-march=armv8.4-a+fp16+dotprod+i8mm")
endif()

# CPU features detection library
add_library(locian_cpu_features SHARED CPUFeatures.cpp)
target_compile_options(
    locian_cpu_features
    PUBLIC
    -fvisibility=hidden -fvisibility-inlines-hidden -ffunction-sections -fdata-sections
)
target_link_libraries(
    locian_cpu_features
    cpu_features android log
)
target_link_options(
    locian_cpu_features
    PRIVATE
    -Wl,--gc-sections -flto
    -Wl,--exclude-libs,ALL
)
