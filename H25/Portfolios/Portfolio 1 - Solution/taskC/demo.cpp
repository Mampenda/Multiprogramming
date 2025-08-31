#include "roadMaintenance.cpp"

int main() {
  srand(time(0));

  const int N = 10;
  processes ps;

  ps += [&]() -> void {
    for (int _ = 0; _ < N; _++)
      processSupervisor();
    std::cout << "All " << N << " boxes assembled" << std::endl;
    exit(0);
  };

  ps += [&]() -> void {
    while (true)
      processCleaningSolution();
  };
  ps += [&]() -> void {
    while (true)
      processPaint();
  };
  ps += [&]() -> void {
    while (true)
      processSensor();
  };
}