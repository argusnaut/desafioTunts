package tunts.test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.UpdateValuesResponse;
import com.google.api.services.sheets.v4.model.ValueRange;

public class TuntsMain {

	private static final String APPLICATION_NAME = "Teste TUNTS"; // Nome da aplica��o.
	private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance(); // Cria��o da JSON Factory padr�o.
	private static final String TOKENS_DIRECTORY_PATH = "tokens"; // Armazenamento da credencial.

	// Escopo da aplica��o. Caso seja alterado � necess�rio deletar o token.
	private static final List<String> SCOPES = Collections.singletonList(SheetsScopes.SPREADSHEETS);
	private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

	/**
	 * Cria��o da credencial para autorizar o objeto.
	 * 
	 * @param httpTransport o HTTP Transport da rede.
	 * @return Um objeto Credential autorizado.
	 * @throws IOException Caso credentials.json n�o seja encontrada.
	 */
	private static Credential getCredentials(final NetHttpTransport httpTransport) throws IOException {
		// Leitura dos segredos de acesso.
		InputStream is = TuntsMain.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
		if (is == null) {
			throw new FileNotFoundException("Recurso n�o encontrado: " + CREDENTIALS_FILE_PATH);
		}
		GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(is));

		// Constru��a do flow e pedido de autoriza��o.
		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, JSON_FACTORY,
				clientSecrets, SCOPES)
						.setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
						.setAccessType("offline").build();
		LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
		return new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");
	}

	/**
	 * Realiza a atualiza��o dos dados na planilha apontada por spreadsheetId.
	 */
	public static void main(String[] args) throws GeneralSecurityException, IOException {
		System.out.println("Iniciando atualiza��o. Por favor, aguarde.");
		System.out.println("...");
		
		//Autoriza��o de acordo com a API.
		final NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		// Sele��o da planilha e range para o uso do servi�o.
		final String spreadsheetId = "1XKB0dtBCLpnKufRnP8w1Q3HJqsYSKKuynbGp1laYJeY";
		final String range = "A4:H27";
		
		// Cria��o da conex�o, conforme API.
		Sheets service = new Sheets.Builder(httpTransport, JSON_FACTORY, getCredentials(httpTransport))
				.setApplicationName(APPLICATION_NAME).build();
		
		// Leitura do range selecionado.
		ValueRange response = service.spreadsheets().values().get(spreadsheetId, range).execute();
		
		// Cria��o da lista que armazena o retorno da planilha.
		List<List<Object>> values = response.getValues();
		if (values == null || values.isEmpty()) {
			System.out.println("Dados n�o encontrados.");
		} else {
			// Cria��o da lista usada na atualiza��o da planilha.
			List<List<Object>> data = new ArrayList<>();

			// La�o de intera��o para percorrer por toda tabela.
			for (List<Object> list : values) {
				// Representa a linha do aluno que ser� atualizado.
				List<Object> lista = new ArrayList<>();
				
				// Cria��o de uma inst�ncia de aluno das linha da planilha.
				Aluno aluno = new Aluno(Integer.parseInt((String) list.get(0)), (String) list.get(1),
						Integer.parseInt((String) list.get(2)), Integer.parseInt((String) list.get(3)),
						Integer.parseInt((String) list.get(4)), Integer.parseInt((String) list.get(5)));
				
				lista.add(aluno.getSituacao());
				lista.add(aluno.getNotaParaAprovacao());
				data.add(lista); // Adi��o do aluna para lista de atualiza��o da tabela.

			}

			// Update do range selecionado do Google Spreadsheets.
			ValueRange body = new ValueRange().setValues(data);
			UpdateValuesResponse result = service.spreadsheets().values().update(spreadsheetId, "G4:H27", body)
					.setValueInputOption("USER_ENTERED").execute();

			System.out.printf("%d c�lulas atualizadas.", result.getUpdatedCells());

		}

	}
}
