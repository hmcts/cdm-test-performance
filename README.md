# cdm-test.performance
Gatling performance tests for CCD

In order to run the CCD Performance tests, you will need to ensure that you have access to one of the performance VMs in order to run the test - performance tests should not be run from your local machine!

1. Clone the repo to your local, ensure that you have Gradle configured and set up
2. Open the project in your preferred IDE (IntelliJ is best though)
3. The simulation file /scenarios/simulations/CCD_PerformanceRegression controls how the test runs, you can change the number of iterations by editing the control values for each scenario
4. If you make changes to the default runtime settings, then you will need to push your changes back to the repo and then clone or *git pull* on the VM (depending if you already have this repo cloned on the VM or not)

To run locally:

Performance test against the perftest environment: `./gradlew gatlingRun`
Flags:

Debug (single-user mode): `-Ddebug=on e.g. ./gradlew gatlingRun -Ddebug=on`
Run against AAT: Denv=aat e.g. `./gradlew gatlingRun -Denv=aat`

To run the DMStore or CaseDocAPI Simulations you will need to manually create the large PDF files - these cannot be stored in Git for obvious reasons

eg. to create a 50mb file you can run `truncate -s 50M 50MB.pdf` locally or from the VM