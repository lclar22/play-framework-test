$ ->
  $.get "/cuentas", (bancos) ->
    $.each bancos, (index, banco) ->
      nombre = $("<td>").text banco.nombre
      tipo = $("<td>").text banco.tipo
      link = $("<td>").html '<a href="/cuentas_update/' + banco.id + '"><span class="glyphicon glyphicon-pencil"></span></a>' + '<a href="/cuentas_update/' + banco.id + '"><span class="glyphicon glyphicon-remove"></span></a>'
      $("#bancos").append $("<tr>").append(nombre).append(tipo).append(link)
