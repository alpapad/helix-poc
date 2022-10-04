### helix-poc


Use the docker-compose file to run the demo after building. 

There are `2 zones` each with one `controller`. Each zone also has 2 `participants` so 4 in total. Then to complete the poc in each zone we have one application which is aware of the `topology` of the cluster. 


The `controllers` orchestrate the cluster `leaders`/`followers` ensuring that at any given time there is exactly one `leader` while the rest are followers. 

The `participants` act like probes, making sure we have enough info on the status of each `zone` (both `participants` will need to be down to consider a `zone` as `down`. Participants can be deployed as part of a kubernetes deployment.


The `spectators` are the actual applications which need to be aware of the `cluster` topology in order to adjust wich functions to perform.


Finally, there is a minimal application which will `bootstrap` the cluster by configuring it.

### Notes


 - Setup of the cluster can be done via the helix command line tools
 - The controller can also be started via the helix command line tools, as it is the vanilla controller and the code is provided by helix.
 - An e-2-e test should be created (using eg test-containers), where all possible scenarios can be tested (one just needs to start/stop containers -- or use rest to start/stop services -- and observe the value of `HelixSpectatorService.getOnLeaderZone()`)
 - For use with spring-boot the spectator code should be extracted to an auto-configurable spring module which then will just need to be included as a maven dependency and configured in application.yml of any spring boot application.
