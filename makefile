CXX       := g++
CXX_FLAGS := -std=c++17 -ggdb

SRC     := code
EXECUTABLE  := EMU


all: $(EXECUTABLE)

$(EXECUTABLE): $(SRC)/*.cpp
	$(CXX) $(CXX_FLAGS) $^ -o $@
