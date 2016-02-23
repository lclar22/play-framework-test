$ ->
  $.get "/cuentas", (bancos) ->
    $.each bancos, (index, banco) ->
      nombre = $("<td>").text banco.nombre
      tipo = $("<td>").text banco.tipo
      $("#bancos").append $("<tr>").append(nombre).append(tipo)
