row_id = location.pathname.split('/')[2]

$ ->
  $.get "/productRequestsByStorekeeper/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      date = $("<td>").text row.date
      veterinario = $("<td>").text row.veterinario
      storekeeper = $("<td>").text row.storekeeper
      status = $("<td>").text row.status
      detail = $("<td>").text row.detail
      links = $("<td>").html '<a href="/productRequest_send/' + row.id + '"><span class="glyphicon glyphicon-pencil">Enviar</span></a>' + '<a href="/productRequest_accept/' + row.id + '"><span class="glyphicon glyphicon-pencil">Aceptar</span></a>' + '<a href="/productRequest_finish/' + row.id + '"><span class="glyphicon glyphicon-pencil">Finalizar</span></a>' + '<a href="/productRequest_update/' + row.id + '"><span class="glyphicon glyphicon-pencil">Editar</span></a>' + '<a href="/productRequest_remove/' + row.id + '"><span class="glyphicon glyphicon-remove">Eliminar</span></a>' + '<a href="/productRequest_show/' + row.id + '"><span class="glyphicon glyphicon-remove">Mostrar</span></a>'
      $("#rows").append $("<tr>").append(date).append(veterinario).append(storekeeper).append(status).append(detail).append(links)
