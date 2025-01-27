import random

MINIMUM_GUESSING_NUMBER_LIMIT = 1
MAXIMUM_GUESSING_NUMBER_LIMIT = 100

def is_valid_guess(user_guess):
    return user_guess.isdigit() and MINIMUM_GUESSING_NUMBER_LIMIT <= int(user_guess) <= MAXIMUM_GUESSING_NUMBER_LIMIT

def get_user_guess():
    return input("Guess a number between 1 and 100: ")

def get_valid_guess():
    while True:
        user_guess = get_user_guess()
        if is_valid_guess(user_guess):
            return int(user_guess)
        else:
            print("Invalid input. Please enter a number between 1 and 100.")

def check_guess(user_guess, random_number):
    if user_guess < random_number:
        print("Too low. Guess again.")
        return False
    elif user_guess > random_number:
        print("Too high. Guess again.")
        return False
    else:
        return True

def main():
    random_number = random.randint(MINIMUM_GUESSING_NUMBER_LIMIT, MAXIMUM_GUESSING_NUMBER_LIMIT)
    guess_count = 0
    is_correct_guess = False

    while not is_correct_guess:
        user_guess = get_valid_guess()
        guess_count += 1
        is_correct_guess = check_guess(user_guess, random_number)

    print(f"You guessed it in {guess_count} guesses!")

if __name__ == "__main__":
    main()