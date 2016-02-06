$ ->
  $.get "/clientes", (clientes) ->
    $.each clientes, (index, cliente) ->
      nombre = $("<div>").addClass("name").text cliente.nombre
      carnet = $("<div>").addClass("last_name").text cliente.carnet
      id_asociacion = $("<div>").addClass("age").text cliente.id_asociacion
      $("#clientes").append $("<li>").append(nombre).append(carnet).append(id_asociacion)