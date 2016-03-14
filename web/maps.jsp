
<html>
  <head>
    <title>Simple Map</title>
    <meta name="viewport" content="initial-scale=1.0">
    <meta charset="utf-8">
    <style>
      html, body {
        height: 100%;
        margin: 0;
        padding: 0;
      }
      #map {
        height: 100%;
      }
    </style>
    
    <link href="css/bootstrap.min.css" rel="stylesheet">
    <script src="js/bootstrap.min.js"></script>
    <script src="js/jquery.js"></script>    
   <script src="js/jquery.csv.js"></script>
  </head>
  <body>
     
    <div class="container">
        <div class="row">
            <div class="col-lg-3">
                <div style='background-color:#FFFFFF; width:92%; min-height: 45px; margin-left: 10px; margin-top:100px'><p style='font-size:16pt; padding-left:5px; padding-top:5px; padding-bottom:5px'>Twitter Sentiments</p></div>
                <div id="twitterScroll" style="overflow:auto; height:80%; width:100%">
                    
                </div>
            </div>
            <div class="col-md-9">         
                <br/><input id="searchTextField" type="text" size="50"><br/><br/>
                <label for="busStopsToggle">Bus Stops</label>
                <input type="checkbox" onclick="updateCriteria();" id="busStopsToggle" />&nbsp;&nbsp;&nbsp;
                <label for="taxiToggle">Taxis</label>
                <input type="checkbox" onclick="updateCriteria();" id="taxiToggle" />&nbsp;&nbsp;&nbsp;
                <label for="taxiStandsToggle">Taxi Stands</label>
                <input type="checkbox" onclick="updateCriteria();" id="taxiStandsToggle" />
                <div id="map" style="width:100%; height:650px"></div> 
            </div>
        </div>
    </div>   
    
    <script>
        
        var twitterData = [];
        <%
            for(int i = 0 ; i < 20; i++){
        %>
            twitterData.push("This is tweet number <%= i %>");
        <%
            }
        %>        
		
        var map;
        var searchRadius = 1; // kilometers
        var curLocationMarker;        
        var curLocationPerimter;
        
        var busCheck;
        var taxiCheck;
        var taxiStandCheck;
        
        var infos;
        var overlayMarkers = [];
        var overlayBusServicesMarkers = [];
        
        var allBusStops = [];
        var selectedBusStops = [];
        
        var selectedServiceBusStop = [];
        
        var allBusRoutes = [];
        var selectedBusRoutes = [];
        
        var allBusStopsServices = [];
        var selectedBusStopsServices = [];
        
        var taxiList = [];
        var selectedTaxis = [];
        
        var placeholderInput = document.getElementById('searchTextField');
        
        function updateCriteria(){
            busCheck = document.getElementById("busStopsToggle").checked;
            taxiCheck = document.getElementById("taxiToggle").checked;
            taxiStandCheck = document.getElementById("taxiStandsToggle").checked; 
        }
        
        function distanceBetweenPoints(lat1, lon1, lat2, lon2) {
            var p = 0.017453292519943295;    // Math.PI / 180
            var c = Math.cos;
            var a = 0.5 - c((lat2 - lat1) * p)/2 + 
                    c(lat1 * p) * c(lat2 * p) * 
                    (1 - c((lon2 - lon1) * p))/2;

            return 12742 * Math.asin(Math.sqrt(a)); // 2 * R; R = 6371 km
        }
        
        function removeOverlayMarkers(){
            for(var c = 0; c < overlayMarkers.length; c++){
                overlayMarkers[c].setMap(null);
            }
            return overlayMarkers;
        }
        
        function removeServiceOverlayMarkers(){
            //alert(overlayBusServicesMarkers.length);
            for(var c = 0; c < overlayBusServicesMarkers.length; c++){
                overlayBusServicesMarkers[c].setMap(null);
            }
            return overlayBusServicesMarkers;
        }
        
        function setTaxis_Marker(selectedTaxis){    // Take in Array            
            for(var i = 0; i < selectedTaxis.length; i++){
                var curLat = parseFloat(selectedTaxis[i][0]);
                var curLng = parseFloat(selectedTaxis[i][1]);
                
                //Create Circle Radius
                taxiCircle = new google.maps.Circle({
                    strokeColor: '#FF0000',
                    strokeOpacity: 0.9,
                    strokeWeight: 2,
                    fillColor: '#FF0000',
                    fillOpacity: 0.8,
                    map: map,
                    center: new google.maps.LatLng(curLat, curLng),
                    radius: 15
                });
                
                overlayMarkers.push(taxiCircle);
            }                 
        }
       
        function setBusStops_Marker(busStops){    // Take in Array
            var busStops_marker = {
                url: 'markers/bus.png',
                size: new google.maps.Size(20,24),
                //origin: new google.maps.Point(0, 0)                
            }
            
            for(var i = 0; i < busStops.length; i++){
                var curLat = parseFloat(busStops[i][3]);
                var curLng = parseFloat(busStops[i][4]);
                var busStopMarker = new google.maps.Marker({                    
                    position: {lat: curLat, lng: curLng},     
                    animation: google.maps.Animation.DROP, // Have BOUNCE
                    icon: busStops_marker,
                    map: map
                });
                
                overlayMarkers.push(busStopMarker);
                
                var infowindow = new google.maps.InfoWindow();
                
                var infoContent = "<p><span style='font-weight:bold'>Bus Stop </span>" + busStops[i][0] + "</p><p><span style='font-weight:bold'>Location  </span>" + busStops[i][2] + "</p>" +
                "<p><span style='font-weight: bold'>Services </span></p><p>";  
                var tempBusStop = busStops[i][0];
               
                var tempBusStopSvc = selectedBusStopsServices["" + tempBusStop];
                for(var b = 1 ; b < tempBusStopSvc.length; b++){                                   
                    infoContent += "<a href='#' onclick='showBusStopsAlongSvc(" + tempBusStopSvc[b] + ");'>" + tempBusStopSvc[b] + "</a>";
                    if(b < (tempBusStopSvc.length - 1)){
                       infoContent += " | ";
                    }
                }                
                infoContent += "</p>";
                
                google.maps.event.addListener(busStopMarker,'click', (function(marker,content,infowindow){ 
                    return function() {
                        closeInfos();
                        infowindow.setContent(content);
                        infowindow.open(map,marker);
                        infos = infowindow;
                    };
                })(busStopMarker,infoContent,infowindow)); 
            }                 
        }
        
        
        function setBusStopsServices_Marker(busStops){    // Take in Array
            var newbusStops_marker = {
                url: 'markers/bus_red.png',
                size: new google.maps.Size(20,24)
                //origin: new google.maps.Point(0, 0)                
            }
            
            for(var i = 0; i < busStops.length; i++){
                var curLat = parseFloat(busStops[i][3]);
                var curLng = parseFloat(busStops[i][4]);
                
                var newbusStopsMarker = new google.maps.Marker({                    
                    position: {lat: curLat, lng: curLng},     
                    animation: google.maps.Animation.DROP, // Have BOUNCE
                    icon: newbusStops_marker,
                    map: map
                });
                
                overlayBusServicesMarkers.push(newbusStopsMarker);
            }                 
        }
      
        function closeInfos(){
            if(infos == null){
                //alert();
            }else{
                infos.set("busStopMarker", null);
                infos.close();
            }
        }
        
        function smoothZoom (map, max, cnt) {
            if (cnt >= max) {
                    return;
                }
            else {
                z = google.maps.event.addListener(map, 'zoom_changed', function(event){
                    google.maps.event.removeListener(z);
                    smoothZoom(map, max, cnt + 1);
                });
                setTimeout(function(){map.setZoom(cnt)}, 80); // 80ms is what I found to work well on my system -- it might not work well on all systems
            }
        }  
        
        function getTaxisWithinRadius(currentPoint){
            
            for(var bs in taxiList){
                var tempLat = parseFloat(taxiList[bs][0]);
                var tempLng = parseFloat(taxiList[bs][1]);
                if(searchRadius > distanceBetweenPoints(currentPoint.lat(), currentPoint.lng(), tempLat, tempLng)){
                    selectedTaxis.push(taxiList[bs]);                    
                }                
            }           
            
        }
        
        function getBusStopsWithinRadius(currentPoint){
            
            selectedBusStops = [];
            //selectedBusStopsServices = [];
            for(var bs in allBusStops){
                var tempLat = parseFloat(allBusStops[bs][3]);
                var tempLng = parseFloat(allBusStops[bs][4]);
                if(searchRadius > distanceBetweenPoints(currentPoint.lat(), currentPoint.lng(), tempLat, tempLng)){
                    selectedBusStops.push(allBusStops[bs]);                    
                }                
            }      
            for(var c = 0 ; c < selectedBusStops.length; c++){
                var t = selectedBusStops[c][0];
                
                selectedBusStopsServices[allBusStopsServices["" + t][0]] = allBusStopsServices["" + t];
            }
            
        }
        
        function showBusStopsAlongSvc(svc){
            selectedServiceBusStop = [];
            removeServiceOverlayMarkers();
            var temp = allBusRoutes[svc + "-1"];
            for(var i = 0 ; i < temp.length; i++){
                selectedServiceBusStop.push(allBusStops["" + temp[i]]);
            }
            
            setBusStopsServices_Marker(selectedServiceBusStop);
        }
        
        function plotCurrentLocation(latLng){
            if(curLocationMarker == null){
                    
            }else{
                curLocationMarker.setMap(null);
                curLocationPerimter.setMap(null);
            }
            curLocationMarker = new google.maps.Marker({
                position: latLng,
                map:map
            });
        }    
    
        function initMap() {       
            map = new google.maps.Map(document.getElementById("map"), {
              center: {lat: 1.3150701, lng: 103.7069303},
              zoom: 13
            });          
            
            busCheck = document.getElementById("busStopsToggle").checked;
            taxiCheck = document.getElementById("taxiToggle").checked;
            taxiStandCheck = document.getElementById("taxiStandsToggle").checked; 
            
            google.maps.event.addListener(map, 'click', function(event){
                selectedTaxis = [];
                        
                removeOverlayMarkers();
                removeServiceOverlayMarkers();
                plotCurrentLocation(event.latLng);
                
                if(busCheck == true){
                    getBusStopsWithinRadius(event.latLng);
                    setBusStops_Marker(selectedBusStops);
                    selectedServiceBusStop = [];        
                }
                if(taxiCheck == true){
                    getTaxisWithinRadius(event.latLng);
                    setTaxis_Marker(selectedTaxis);
                }
                
                //Create Circle Radius
                curLocationPerimter = new google.maps.Circle({
                    strokeColor: '#3333cc',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#3333cc',
                    fillOpacity: 0.35,
                    map: map,
                    center: event.latLng,
                    radius: (searchRadius * 1000)
                });
                                
                map.panTo(event.latLng);
                smoothZoom(map, 16, map.getZoom());                
                
            });
            
            var autocomplete = new google.maps.places.Autocomplete(placeholderInput);
            google.maps.event.addListener(autocomplete, 'place_changed', function() {
                
                selectedTaxis = [];
                //selectedServiceBusStop = [];                
                removeOverlayMarkers();
                removeServiceOverlayMarkers();
                plotCurrentLocation(autocomplete.getPlace().geometry.location);
                
                if(busCheck == true){
                    getBusStopsWithinRadius(autocomplete.getPlace().geometry.location);                    
                    setBusStops_Marker(selectedBusStops);
                    selectedServiceBusStop = []; 
                }
                if(taxiCheck == true){
                    getTaxisWithinRadius(autocomplete.getPlace().geometry.location);
                    setTaxis_Marker(selectedTaxis);
                }
               
                //Create Circle Radius
                curLocationPerimter = new google.maps.Circle({
                    strokeColor: '#3333cc',
                    strokeOpacity: 0.8,
                    strokeWeight: 2,
                    fillColor: '#3333cc',
                    fillOpacity: 0.35,
                    map: map,
                    center: autocomplete.getPlace().geometry.location,
                    radius: (searchRadius * 1000)
                });
                                
                map.panTo(autocomplete.getPlace().geometry.location);
                smoothZoom(map, 16, map.getZoom());
                
            });
        }
        
        //Bus Stops CSV Data
        $.ajax({url: "resources/BusStop.csv", success: function(file_content){
                var tempBusStops = [];
                tempBusStops = $.csv.toArrays(file_content); 
                tempBusStops = tempBusStops.slice(1, 5000); 
                for(var i = 1 ; i < tempBusStops.length; i++){
                    allBusStops[tempBusStops[i][0]] = tempBusStops[i];                    
                }                
                
            }
        });
        
        //Bus Routes CSV Data
        $.ajax({url: "resources/BusRoutes.csv", success: function(file_content){
                var tempBusRoutes = [];
                tempBusRoutes = $.csv.toArrays(file_content); 
                tempBusRoutes = tempBusRoutes.slice(1, 23428); 
                
                var bus = tempBusRoutes[1][0];
                var dir = tempBusRoutes[1][2];
                
                
                var bs = [];
                var dr = [];
                var sv = [];
                
                for(var i = 2 ; i < tempBusRoutes.length; i++){
                    if(bus === tempBusRoutes[i][0]){
                        // Still within the same svc
                        if(dir === tempBusRoutes[i][2]){
                            bs.push(tempBusRoutes[i][4]); 
                        }else{
                            // Different direction
                            dr[dir] = bs;
                            bs = [];
                            bs.push(tempBusRoutes[i][4]); 
                        }
                    }else{
                        // New Service
                        dr[dir] = bs
                        bs = [];
                        for(var d = 1 ; d <= 2; d++){
                            allBusRoutes[tempBusRoutes[i - 1][0] + "-" + d] = dr[d];                            ;
                        }
                    }
                    bus = tempBusRoutes[i][0];
                    dir = tempBusRoutes[i][2];                    
                } 
               
            }
        });
        
        //Bus Stops Services CSV Data
        $.ajax({url: "resources/BusStopServices.csv", success: function(file_content){
                var tempBusStopsServices = [];
                tempBusStopsServices = $.csv.toArrays(file_content); 
                tempBusStopsServices = tempBusStopsServices.slice(1, 23428); 
                for(var c = 1 ; c < tempBusStopsServices.length; c++){
                    allBusStopsServices["" + tempBusStopsServices[c][0]] = tempBusStopsServices[c];
                }                
            }
        });
        
        //Taxis Location CSV Data
        $.ajax({url: "resources/Taxi_1.csv", success: function(file_content){
                taxiList = [];
                taxiList = $.csv.toArrays(file_content);  
                taxiList = taxiList.slice(1, taxiList.length);
            }
        });
        
        $(document).ready(function(){
            var chk = true;
            for(var x = 0 ; x < twitterData.length; x++){
                if(chk){
                    chk = false;
                    var data = "<div style='background-color:#abc8d3; width:92%; min-height: 45px; margin-left: 10px'><p style='padding-left:5px; padding-top:5px; padding-bottom:5px'>" + twitterData[x]  + "</p></div>";
                
                }else{
                    chk = true;
                    var data = "<div style='background-color:#E5E4E2; width:92%; min-height: 45px; margin-left: 10px'><p style='padding-left:5px; padding-top:5px; padding-bottom:5px'>" + twitterData[x]  + "</p></div>";
                
                }
                $('#twitterScroll').append(data);
            }
            
        });
	
	  
	  
    </script>
    <script src="https://maps.googleapis.com/maps/api/js?key=AIzaSyBNaPijosJaw1BkDCNb-RnAefrN8WdA9OQ&callback=initMap&libraries=places"></script>
  </body>
</html>