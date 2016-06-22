package com.thecyclingapp.emiliyan.thecyclingapp.activities;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thecyclingapp.emiliyan.thecyclingapp.R;
import com.thecyclingapp.emiliyan.thecyclingapp.json.AddNewUser;

public class TestConnection extends AppCompatActivity {
    private Button button;
    private TextView status;
    Activity activity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_connection);
        linkComponents();
    }


    public void linkComponents() {
        status = (TextView) findViewById(R.id.status);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddNewUser anu = new AddNewUser(activity.getApplicationContext()) {
                    @Override
                    public void onResponseReceived(String response) {

                    }
                };

                String[] firstname = {"Theresa", "Caroline", "Ryan", "Sarah", "Leonard", "Leah", "Ava", "Isaac", "Una", "Sally", "Sebastian",
                        "Sophie", "Emily", "Colin", "Luke", "Wendy", "Irene", "Ian", "Emily", "Isaac", "Olivia", "Carolyn", "Faith", "Wanda",
                        "Liam", "Elizabeth", "Evan", "Bella", "John", "Rose", "Alexandra", "Trevor", "Jane", "Bernadette", "Dan", "Victoria",
                        "Peter", "Jack", "Jack", "Joan", "Audrey", "Julia", "Alison", "Fiona", "Nathan", "Alexander", "Lisa", "Katherine",
                        "Mary", "Carl", "Rachel", "Carl", "Trevor", "Peter", "Kevin", "Alexandra", "Dylan", "Sally", "Chloe", "Sean",
                        "Dorothy", "Carl", "Nicholas", "Gavin", "Alan", "Heather", "Colin", "Austin", "Sean", "William", "David", "Sophie",
                        "Carolyn", "Peter", "Fiona", "Connor", "Stewart", "Karen", "Stephen", "Felicity", "Ruth", "Alison", "Dan", "Stephen",
                        "Colin", "Audrey", "Bella", "Kimberly", "Carl", "Carol", "Colin", "Gavin", "Joseph", "Owen", "Trevor", "Melanie",
                        "Irene", "Julia", "Nicola", "Benjamin", "Angela", "Irene", "Brian", "Stephanie", "Sam", "Brandon", "Sally", "Emma",
                        "Colin", "Oliver", "Trevor", "Pippa", "Kevin", "Katherine", "Owen", "Irene", "Owen", "Gordon", "Joan", "Sebastian",
                        "Abigail", "Wendy", "Fiona", "Joe", "Jasmine", "Lily", "Claire", "Dan", "Dominic", "Andrew", "Nathan", "Sarah",
                        "Molly", "Peter", "Kylie", "Sophie", "Rose", "Diana", "Gabrielle", "Jennifer", "Samantha", "Rachel", "Dorothy",
                        "Anna", "Nicholas", "Elizabeth", "Charles", "Keith", "Oliver", "Liam", "Andrew", "Fiona", "John", "Rose", "Keith",
                        "Benjamin", "Dorothy", "Sean", "Wanda", "Jasmine", "Gavin", "Melanie", "Anne", "Audrey", "Sebastian", "Bella",
                        "Thomas", "Carol", "Paul", "Melanie", "Yvonne", "Joanne", "Victoria", "Penelope", "Eric", "Owen", "Una", "Nicola",
                        "Molly", "Christian", "Max", "Andrea", "Robert", "Jason", "Cameron", "Thomas",
                        "Julia", "Joe", "Gavin", "Ruth", "Madeleine", "Abigail", "Harry", "Joshua", "Lisa", "Sonia", "Lucas", "Olivia", "Warren"};








                String[] lastnames={"Morrison", "Turner", "North", "Davidson", "Wilson", "Davidson", "Rutherford", "Chapman", "May", "Paige",
                        "Ross", "Lewis", "Ball", "Anderson", "Rutherford", "Fisher", "MacLeod", "Edmunds", "Morrison", "Marshall", "Parr",
                        "Cameron", "Carr", "Peters", "Murray", "Hardacre", "Edmunds", "Vaughan", "Robertson", "Ferguson", "Piper", "Skinner",
                        "Avery", "May", "Peake", "McLean", "Clarkson", "Powell", "Butler", "Welch", "Manning", "Vance", "Marshall", "Blake",
                        "Wallace", "Blake", "Clark", "Vance", "Vance", "Greene", "Davies", "Hunter", "Bailey", "Langdon", "Hemmings", "McLean",
                        "North", "Allan", "Morrison", "Clark", "Quinn", "Churchill", "Hudson", "Gill", "Davidson", "Davidson", "Ogden",
                        "Hemmings", "Cameron", "Bower", "Paige", "Jones", "Nash", "James", "Nolan", "Nolan", "Nash", "Hemmings",
                        "Simpson", "Vaughan", "Brown", "Butler", "Smith", "Anderson", "Lyman", "Morrison", "Davies", "Payne", "Henderson",
                        "Clarkson", "Arnold", "Dyer", "Grant", "Randall", "Gibson", "Chapman", "Coleman", "Graham", "Smith", "Fisher",
                        "Lambert", "Fraser", "Glover", "Rees", "Watson", "Morgan", "Knox", "Sutherland", "Peters", "Roberts", "Paige",
                        "Lee", "Churchill", "Cameron", "Vaughan", "Cameron", "Morgan", "May", "Martin", "Graham", "Short", "Gibson",
                        "Short", "Sharp", "Bailey", "Allan", "Ferguson", "Payne", "Black", "Hardacre", "Nolan", "Abraham", "Rampling",
                        "Dowd", "Scott", "Cornish", "Mitchell", "Wilkins", "Hamilton", "Mackenzie", "Reid", "Hemmings", "Lyman",
                        "Sanderson", "Coleman", "Sanderson", "Reid", "King", "Wilkins", "Ferguson", "Duncan", "Mills", "Clark",
                        "Robertson", "Hemmings", "Parr", "McGrath", "Terry", "Campbell", "Buckland", "Wright", "Randall", "Hill",
                        "Greene", "Mitchell", "Thomson", "Brown", "Vance", "Ogden", "Wright", "McGrath", "Fraser", "James", "Alsop",
                        "Clark", "Butler", "Forsyth", "MacLeod", "Newman", "Fisher", "Burgess", "Piper", "Ince", "Clark", "Poole", "Bell",
                        "MacDonald", "Miller", "Davidson", "Avery", "May", "Rampling", "Turner", "Walsh", "Short", "Smith", "Ogden", "Randall", "Carr"};

                int min = Math.min(firstname.length,lastnames.length);
                for (int i = 0 ; i<min;i++){
                    anu.sendUsernameCheckQuery(firstname[i],"pass",firstname[i]+"@abv.bg",firstname[i],lastnames[i]);
                }

            }
        });
    }

    public void makePostConnection(){

    }



}
