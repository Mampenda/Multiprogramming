# The minimum required version of CMake
cmake_minimum_required(VERSION 3.14...4.1)

# Project details
project(
  awaitTests
  VERSION 1.0
  LANGUAGES CXX)

# Require C++20
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED True)
# set(CMAKE_RUNTIME_OUTPUT_DIRECTORY ./..)

# Add the -Wall compile flag.
set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -Wall -Wextra")

Include(FetchContent)

FetchContent_Declare(
  googletest
  URL https://github.com/google/googletest/archive/03597a01ee50ed33e9dfd640b249b4be3799d395.zip
)
set(gtest_force_shared_crt ON CACHE BOOL "" FORCE)
FetchContent_MakeAvailable(googletest)

enable_testing()


# Add executable to be built
# add_executable(${PROJECT_NAME} demo.cpp)

add_executable(${PROJECT_NAME} testRoadMaint.cpp)


target_link_libraries(${PROJECT_NAME} GTest::gtest_main)


include(GoogleTest)
gtest_add_tests(TARGET ${PROJECT_NAME} TEST_LIST testList)

set_tests_properties(${testList} PROPERTIES TIMEOUT 5)
