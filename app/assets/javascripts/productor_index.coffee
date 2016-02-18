$ ->
  $.get "/productores", (productores) ->
    $.each productores, (index, productor) ->
      nombre = $("<td>").text productor.nombre
      carnet = $("<td>").text productor.carnet
      asociacion = $("<td>").text productor.asociacion
      $("#productores").append $("<tr>").append(nombre).append(carnet).append(asociacion)