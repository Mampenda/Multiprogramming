#include "alang.h"

semaphore semCleaningSolution = 0, semPaint = 0, semSensor = 0, semSupervisor = 0;

void processSupervisor() {
  int randNum = randomProcess();
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
  semSupervisor.P();
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