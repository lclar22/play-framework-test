$ ->
  $.get "/cuentas", (bancos) ->
    $.each bancos, (index, banco) ->
      nombre = $("<td>").text banco.nombre
      tipo = $("<td>").text banco.tipo
      link = $("<td>").html '<a href="/cuentas_update/' + banco.id + '"><span class="glyphicon glyphicon-pencil">Editar</span></a>' + '<a href="/cuentas_update/' + banco.id + '"><span class="glyphicon glyphicon-remove">Eliminar</span></a>'
      $("#bancos").append $("<tr>").append(nombre).append(tipo).append(link)
