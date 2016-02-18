$ ->
  $.get "/bancos", (bancos) ->
    $.each bancos, (index, banco) ->
      monto = $("<td>").text banco.monto
      cuenta = $("<td>").text banco.cuenta
      cliente = $("<td>").text banco.cliente
      $("#bancos").append $("<tr>").append(monto).append(cuenta).append(cliente)
