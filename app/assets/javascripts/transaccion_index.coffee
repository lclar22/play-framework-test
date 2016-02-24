$ ->
  $.get "/transacciones", (transacciones) ->
    $.each transacciones, (index, trasaccion) ->
      fecha = $("<td>").text trasaccion.fecha
      descripcion = $("<td>").text trasaccion.descripcion
      link_view = $("<td>").html '<a href="/transaccion_view"> View </a>'
      link_details = $("<td>").html '<a href="/transacciones"> Details  </a>'
      $("#transacciones").append $("<tr>").append(fecha).append(descripcion).append(link_view).append(link_details)