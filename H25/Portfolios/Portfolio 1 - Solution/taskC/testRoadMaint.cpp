#include "roadMaintenance.cpp"
#include <algorithm>
#include <gtest/gtest.h>
#include <ranges>
#include <unistd.h>
namespace ranges = std::ranges;

int main(int argc, char **argv) {
  testing::InitGoogleTest(&argc, argv);
  return RUN_ALL_TESTS();
}

TEST(ProcessTests, testProcessOrder) {
  RecordProperty("cg_name", "Simple case to check order of operation");
  *getNumberList() = {1, 2, 3};
  std::thread t1([&]() -> void {
    for (int _ = 0; _ < 3; _++)
      processSupervisor();
  });
  std::thread t2(processCleaningSolution);

  t2.join();

  EXPECT_TRUE(ranges::find(*getProduced(), "S put paint into the box") !=
              getProduced()->end());
  EXPECT_TRUE(ranges::find(*getProduced(), "S put sensor into the box") !=
              getProduced()->end());
  EXPECT_EQ(getProduced()->at(2), "A put cleaning solution into the box");
  EXPECT_EQ(getAssembled()->at(0), "A assembled the box");
  usleep(100);
  EXPECT_EQ(getProduced()->size(), 5);
  EXPECT_EQ(getAssembled()->size(), 1);

  getProduced()->erase(getProduced()->begin(), getProduced()->begin() + 3);

  std::thread t3(processPaint);

  t3.join();

  EXPECT_TRUE(
      ranges::find(*getProduced(), "S put cleaning solution into the box") !=
      getProduced()->end());
  EXPECT_TRUE(ranges::find(*getProduced(), "S put sensor into the box") !=
              getProduced()->end());
  EXPECT_EQ(getProduced()->at(2), "B put paint into the box");
  EXPECT_EQ(getAssembled()->at(1), "B assembled the box");
  usleep(100);
  EXPECT_EQ(getProduced()->size(), 5);
  EXPECT_EQ(getAssembled()->size(), 2);

  getProduced()->erase(getProduced()->begin(), getProduced()->begin() + 3);

  std::thread t4(processSensor);

  t1.join();
  t4.join();

  EXPECT_TRUE(
      ranges::find(*getProduced(), "S put cleaning solution into the box") !=
      getProduced()->end());
  EXPECT_TRUE(ranges::find(*getProduced(), "S put paint into the box") !=
              getProduced()->end());
  EXPECT_EQ(getProduced()->at(2), "C put sensor into the box");
  EXPECT_EQ(getAssembled()->at(2), "C assembled the box");

  EXPECT_EQ(getProduced()->size(), 3);
  EXPECT_EQ(getAssembled()->size(), 3);
}

TEST(ProcessTests, testBigCase) {
  RecordProperty("cg_name", "Big testcase with randomized order");
  std::random_device rnd_device;
  std::mt19937 mersenne_engine{rnd_device()};
  std::uniform_int_distribution dist(333, 666);

  int rounds = dist(mersenne_engine);

  getNumberList()->clear();
  getNumberList()->reserve(rounds * 3);
  getProduced()->reserve(rounds * 9);
  getAssembled()->reserve(rounds * 3);

  for (int _ = 0; _ < rounds; _++) {
    getNumberList()->push_back(1);
    getNumberList()->push_back(2);
    getNumberList()->push_back(3);
  }

  ranges::shuffle(*getNumberList(), mersenne_engine);
  std::vector<std::thread> threads;

  std::thread t1([&]() -> void {
    for (int _ = 0; _ < rounds * 3; _++)
      processSupervisor();
  });
  std::thread t2([&]() -> void {
    for (int _ = 0; _ < rounds; _++)
      processCleaningSolution();
  });
  std::thread t3([&]() -> void {
    for (int _ = 0; _ < rounds; _++)
      processPaint();
  });
  std::thread t4([&]() -> void {
    for (int _ = 0; _ < rounds; _++)
      processSensor();
  });

  t1.join();
  t2.join();
  t3.join();
  t4.join();

  EXPECT_EQ(getAssembled()->size(), rounds * 3);
  EXPECT_EQ(getProduced()->size(), rounds * 9);
}

// Add tests for correct usage of semaphores