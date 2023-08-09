JAVAC=/usr/bin/javac
JAVA=/usr/bin/java
.SUFFIXES: .java .class

SRCDIR=Parallel/src/MonteCarloMini
BINDIR=Parallel/bin

$(BINDIR)/MonteCarloMini/%.class: $(SRCDIR)/%.java 
	$(JAVAC) -d $(BINDIR) -cp $(BINDIR) -sourcepath $(SRCDIR) $<

CLASSES=TerrainArea.class Search.class MonteCarloMinimizationParallel.class
CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/MonteCarloMini/%.class)

default: $(CLASS_FILES)
clean:
	rm $(BINDIR)/MonteCarloMini/*.class
run: $(CLASS_FILES)
	java -cp Serial/bin MonteCarloMini/MonteCarloMinimization 600 600 -10 10 -50 50 0.5
	java -cp $(BINDIR) MonteCarloMini/MonteCarloMinimizationParallel 600 600 -10 10 -50 50 0.5
javadoc:
	javadoc -d docs -cp $(BINDIR) -sourcepath src/ MonteCarloMini
