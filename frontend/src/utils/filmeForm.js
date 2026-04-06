export function createEmptyFilmeForm() {
  return {
    titulo: '',
    diretor: '',
    ano: null,
    genero: '',
    disponivel: true,
  }
}

export function createFilmeFormState(filme) {
  if (!filme) {
    return createEmptyFilmeForm()
  }

  return {
    titulo: filme.titulo ?? '',
    diretor: filme.diretor ?? '',
    ano: filme.ano ?? null,
    genero: filme.genero ?? '',
    disponivel: filme.disponivel ?? true,
  }
}

export function validateFilmeForm(form) {
  const errors = {}

  if (!form.titulo?.trim()) errors.titulo = 'Título é obrigatório'
  if (!form.diretor?.trim()) errors.diretor = 'Diretor é obrigatório'
  if (!form.genero?.trim()) errors.genero = 'Gênero é obrigatório'

  if (form.ano === null || form.ano === undefined || form.ano === '') {
    errors.ano = 'Ano é obrigatório'
  } else if (Number(form.ano) < 1888) {
    errors.ano = 'Ano deve ser no mínimo 1888'
  }

  return errors
}
