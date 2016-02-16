$ ->
  $.get "/trasacciones", (trasacciones) ->
    $.each trasacciones, (index, trasaccion) ->
      monto = $("<td>").text trasaccion.monto
      cuenta = $("<td>").text trasaccion.cuenta
      cliente = $("<td>").text trasaccion.cliente
      $("#trasacciones").append $("<tr>").append(monto).append(cuenta).append(cliente)