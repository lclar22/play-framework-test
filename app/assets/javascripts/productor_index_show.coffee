productor_id = location.pathname.split('/')[2]

$ ->
  $.get "/productores/" + productor_id, (productores) ->
    $.each productores, (index, productor) ->
      $("#name").html productor.nombre
      $("#type").html productor.id
