$ ->
  $.get "/transacciones", (transacciones) ->
    $.each transacciones, (index, trasaccion) ->
      fecha = $("<div>").html('Fecha: ' + trasaccion.fecha)
      descripcion = $("<div>").html('Descripcion: ' + trasaccion.descripcion)
      $("#transaccion").append $("<div>").append(fecha).append(descripcion)