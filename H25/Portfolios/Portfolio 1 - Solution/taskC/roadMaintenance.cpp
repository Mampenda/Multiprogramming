#include "alang.h"

semaphore semCleaningSolution = 0, semPaint = 0, semSensor = 0,
          semSupervisor = 1;

void processSupervisor() {
  int randNum = randomProcess();
  semSupervisor.P();
  if (randNum == 1) {
    produce("S", "paint");
    produce("S", "sensor");
    semCleaningSolution.V();
  } else if (randNum == 2) {
    produce("S", "cleaning solution");
    produce("S", "sensor");
    semPaint.V();
  } else if (randNum == 3) {
    produce("S", "cleaning solution");
    produce("S", "paint");
    semSensor.V();
  } else {
    throw std::invalid_argument("Invalid value.");
  }
}

void processCleaningSolution() {
  semCleaningSolution.P();
  produce("A", "cleaning solution");
  assemble("A");
  semSupervisor.V();
}

void processPaint() {
  semPaint.P();
  produce("B", "paint");
  assemble("B");
  semSupervisor.V();
}

void processSensor() {
  semSensor.P();
  produce("C", "sensor");
  assemble("C");
  semSupervisor.V();
}

// TEST(HelloTest, BasicAssertions) {
//   // Expect two strings not to be equal.
//   EXPECT_STRNE("hello", "world");
//   // Expect equality.
//   EXPECT_EQ(7 * 6, 42);
// }

// TEST(ProcessTest, LiquidTest) {
//   playerLiquid = 0, playerMug = 0, playerAlmonds = 0, barista = 0, table = 1;

//   EXPECT_EQ(7 * 6, 42);
//   // SECTION("CallingProcessPlayerLiquid") {
//   //   REQUIRE(playerLiquid.checkCount() == 0);
//   //   playerLiquid.V();
//   //   processPlayerLiquid();

//   //   REQUIRE(playerLiquid.checkCount() == 0);
//   //   REQUIRE(table.checkCount() == 1);
//   //   REQUIRE(barista.checkCount() == 1);
// }
// }
//   SECTION("CallingProcessPlayerMug") {
//     REQUIRE(playerMug.checkCount() == 0);
//     playerMug.V();
//     processPlayerMug();

//     REQUIRE(playerMug.checkCount() == 0);
//     REQUIRE(table.checkCount() == 1);
//     REQUIRE(barista.checkCount() == 1);
//   }

//   SECTION("CallingProcessPlayerAlmonds") {
//     REQUIRE(playerAlmonds.checkCount() == 0);
//     playerAlmonds.V();
//     processPlayerAlmonds();

//     REQUIRE(playerAlmonds.checkCount() == 0);
//     REQUIRE(table.checkCount() == 1);
//     REQUIRE(barista.checkCount() == 1);
//   }
// }
